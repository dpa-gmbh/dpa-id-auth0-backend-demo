import {Construct} from 'constructs'
import {BaseService, FargateService, ICluster, TaskDefinition} from 'aws-cdk-lib/aws-ecs'
import {ISecurityGroup, IVpc, SubnetType} from 'aws-cdk-lib/aws-ec2'
import {IApplicationTargetGroup} from 'aws-cdk-lib/aws-elasticloadbalancingv2'

export interface DpaIdAuth0BackendDemoProperties {
    cluster: ICluster
    taskDefinition: TaskDefinition
    securityGroup: ISecurityGroup
    vpc: IVpc
    stackSuffix: string
}

export class DpaIdAuth0BackendDemo extends Construct {
    private readonly service: BaseService

    constructor(scope: Construct, props: DpaIdAuth0BackendDemoProperties) {
        super(scope, 'DpaIdAuth0BackendDemo')
        const {cluster, taskDefinition, securityGroup, vpc} = props

        this.service = new FargateService(this, 'Service', {
            cluster,
            taskDefinition,
            serviceName: `dpa-id-auth0-backend-demo`,
            securityGroups: [securityGroup],
            vpcSubnets: vpc.selectSubnets({subnetType: SubnetType.PRIVATE_WITH_EGRESS})
        })

        this.service.autoScaleTaskCount({
            minCapacity: 1,
            maxCapacity: 2
        })
    }

    public attachToApplicationTargetGroup(
        targetGroup: IApplicationTargetGroup
    ): void {
        this.service.attachToApplicationTargetGroup(targetGroup)
    }
}
