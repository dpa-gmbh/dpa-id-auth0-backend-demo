import {
    Configuration,
    DeploymentSettings,
    DnsSettings,
    Settings,
} from "../configuration";
import {Duration} from "aws-cdk-lib";

export class MasterConfig implements Configuration {
    public static readonly STAGE = "master";

    private static readonly BaseDomain = "dpa-id.de";
    private static readonly HostedZoneId = 'Z2MRBHSLXAT5CM'

    stage = MasterConfig.STAGE;
    env = {account: "202797282286", region: "eu-central-1"};

    settings: Settings;

    constructor() {
        this.settings = this.createSettings();
    }

    public createSettings(): Settings {
        return {
            vpc: {
                name: "development-dpa-id",
            },
            ecsClusterSettings: this.createEcsClusterSettings(),
            loadbalancerSettings: this.createLoadbalancerSettings(),
            dnsSettings: this.createDnsSettings(),
            deploymentSettings: this.createDeploymentSettings(),
        };
    }

    private createLoadbalancerSettings() {
        return {
            securityGroupId: "sg-06d6fa1449018b133",
            loadbalancerArn: "arn:aws:elasticloadbalancing:eu-central-1:202797282286:loadbalancer/app/development-dpa-id-alb/86e923f20175cc16",
            loadbalancerCanonicalHostedZoneId: "Z215JYRZR1TBD5",
            loadbalancerDnsName: "development-dpa-id-alb-1800181711.eu-central-1.elb.amazonaws.com",
            loadbalancerListenerArn: "arn:aws:elasticloadbalancing:eu-central-1:202797282286:listener/app/development-dpa-id-alb/86e923f20175cc16/b11444453b64f67e"
        }
    }

    private createEcsClusterSettings() {
        return {
            arn: "arn:aws:ecs:eu-central-1:202797282286:cluster/dpa-id-services-devel-cluster",
            name: "dpa-id-services-devel-cluster"
        }
    }

    private createDnsSettings(): DnsSettings {
        const recordName = `backend-demo.${MasterConfig.BaseDomain}`;

        return {
            hostedZoneName: MasterConfig.BaseDomain,
            hostedZoneId: MasterConfig.HostedZoneId,
            recordName
        };
    }

    private createDeploymentSettings(): DeploymentSettings {
        return {
            repositoryArn:
                "arn:aws:ecr:eu-central-1:202797282286:repository/dpa-id-auth0-backend-demo",

            applicationPort: 8080,

            /**
             * This is the time, ECS waits for requests to finish before shutting down the container during deployments.
             * On DEV a short delay will result in faster deployments while accepting request droppings.
             */
            deregistrationDelay: Duration.seconds(5),

            /**
             * Defines the priority of this listener rule in the shared load balancer.
             */
            lbPriority: getRandomInt(600, 650),


            /**
             * Stage suffix to support deployment of multiple versions
             */
            stageSuffix: "master",
            /**
             * Memory in MB.
             */
            memoryLimit: 1024,

            /**
             * CPU Units. See: https://docs.aws.amazon.com/cdk/api/latest/docs/@aws-cdk_aws-ecs.TaskDefinitionProps.html#cpu
             */
            cpu: 512,

            /**
             * Defines which profile to use and which configuration file to read.
             * See: https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-profiles
             */
            springProfile: 'master',

            /**
             * Additional environment variables exposed to the application.
             */
            environment: {
                "STAGE": "master"
            },
        };
    }
}

function getRandomInt(min: number, max: number) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min;
}
