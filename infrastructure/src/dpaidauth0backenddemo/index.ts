import {Construct} from 'constructs'

import {Duration, Environment, Stack, StackProps} from 'aws-cdk-lib'
import {ISecurityGroup, IVpc, SecurityGroup, Vpc} from 'aws-cdk-lib/aws-ec2'
import {DpaIdAuth0BackendDemoDnsRecord} from './dns-record'
import {Settings} from '../config/configuration'
import {HostedZone, IHostedZone} from 'aws-cdk-lib/aws-route53'
import {DpaIdAuth0BackendDemoRole} from './role'
import {DpaIdAuth0BackendDemoTaskDefinition} from './task-definition'
import {DpaIdAuth0BackendDemo} from './service'
import {
    ApplicationListener,
    ApplicationLoadBalancer,
    ApplicationProtocol,
    ApplicationTargetGroup,
    IApplicationListener,
    ILoadBalancerV2,
    ListenerCondition,
    TargetType
} from 'aws-cdk-lib/aws-elasticloadbalancingv2'
import {Cluster, ICluster} from "aws-cdk-lib/aws-ecs";


export interface DpaIdAuth0BackendDemoProperties extends StackProps {
    stackSuffix: string
    env: Environment
    settings: Settings
}

export class DpaIdAuth0BackendDemoStack extends Stack {
    private readonly dnsRecord: DpaIdAuth0BackendDemoDnsRecord

    constructor(scope: Construct, id: string, props: DpaIdAuth0BackendDemoProperties) {
        super(scope, id, props)
        const {settings, stackSuffix} = props
        const {dnsSettings} = settings
        const {recordName} = dnsSettings

        const vpc = this.lookupVPC(settings.vpc.name);
        const zone = this.lookupDnsZone(dnsSettings.hostedZoneName);
        const loadBalancerSecurityGroup = this.lookupLBSecurityGroup(
            settings.loadbalancerSettings.securityGroupId
        );
        const loadBalancer = this.lookupLB(
            loadBalancerSecurityGroup,
            vpc,
            settings.loadbalancerSettings.loadbalancerArn,
            settings.loadbalancerSettings.loadbalancerCanonicalHostedZoneId,
            settings.loadbalancerSettings.loadbalancerDnsName
        );
        const cluster = this.lookupCluster(
            vpc,
            settings.ecsClusterSettings.arn,
            settings.ecsClusterSettings.name
        );
        const loadBalancerListener = this.lookupLBListener(
            loadBalancerSecurityGroup,
            settings.loadbalancerSettings.loadbalancerListenerArn
        );

        this.dnsRecord = new DpaIdAuth0BackendDemoDnsRecord(this, {
            loadBalancer: loadBalancer,
            recordName,
            zone: zone
        })
        const applicationRole = new DpaIdAuth0BackendDemoRole(this)

        const taskDefinition = new DpaIdAuth0BackendDemoTaskDefinition(this,
            'ApplicationTaskDefinition',
            {
                settings: settings,
                applicationRole: applicationRole.instance,
                stackSuffix: stackSuffix
            });


        const service = new DpaIdAuth0BackendDemo(this, {
            vpc: vpc,
            taskDefinition: taskDefinition.instance,
            cluster: cluster,
            securityGroup: loadBalancerSecurityGroup,
            stackSuffix
        })

        const targetGroup = new ApplicationTargetGroup(this, 'AppTargetGroup', {
            vpc,
            targetType: TargetType.IP,
            protocol: ApplicationProtocol.HTTP,
            port: settings.deploymentSettings.applicationPort,
            deregistrationDelay: settings.deploymentSettings.deregistrationDelay,
            healthCheck: {
                path: '/actuator/health',
                port: settings.deploymentSettings.applicationPort.toString(),
                unhealthyThresholdCount: 5,
                healthyThresholdCount: 5,
                interval: Duration.seconds(30)
            }
        })

        service.attachToApplicationTargetGroup(targetGroup)

        loadBalancerListener.addTargetGroups("TargetGroup", {
            priority: settings.deploymentSettings.lbPriority,
            conditions: [ListenerCondition.hostHeaders([recordName])],
            targetGroups: [targetGroup],
        });
    }

    private lookupCluster(
        vpc: IVpc,
        clusterArn: string,
        clusterName: string
    ): ICluster {
        return Cluster.fromClusterAttributes(this, "DpaIdCluster", {
            clusterArn,
            clusterName,
            vpc,
            securityGroups: [],
        });
    }

    private lookupVPC(vpcName: string): IVpc {
        return Vpc.fromLookup(this, "DpaIdVPC", {vpcName});
    }

    private lookupLBSecurityGroup(securityGroupId: string): ISecurityGroup {
        return SecurityGroup.fromSecurityGroupId(
            this,
            "DpaIdLoadbalancerSecurityGroup",
            securityGroupId
        );
    }

    private lookupLB(
        securityGroup: ISecurityGroup,
        vpc: IVpc,
        loadBalancerArn: string,
        loadBalancerCanonicalHostedZoneId: string,
        loadBalancerDnsName: string
    ): ILoadBalancerV2 {
        const {securityGroupId} = securityGroup;
        return ApplicationLoadBalancer.fromApplicationLoadBalancerAttributes(
            this,
            "DpaId-LB",
            {
                loadBalancerArn,
                loadBalancerCanonicalHostedZoneId,
                loadBalancerDnsName,
                securityGroupId,
                vpc,
            }
        );
    }

    private lookupLBListener(
        securityGroup: ISecurityGroup,
        listenerArn: string
    ): IApplicationListener {
        return ApplicationListener.fromApplicationListenerAttributes(
            this,
            "DpaId-HTTPs-Listener",
            {
                listenerArn,
                securityGroup,
            }
        );
    }

    private lookupDnsZone(hostedZoneName: string): IHostedZone {
        return HostedZone.fromLookup(this, "HostedZone", {
            domainName: hostedZoneName,
        });
    }
}
