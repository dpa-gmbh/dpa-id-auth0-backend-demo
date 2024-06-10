import {Construct} from 'constructs'
import {IRole} from 'aws-cdk-lib/aws-iam'
import {
    ContainerImage,
    FargateTaskDefinition,
    LogDriver,
    Protocol
} from 'aws-cdk-lib/aws-ecs'
import {Settings} from '../config/configuration'
import {LogGroup, RetentionDays} from 'aws-cdk-lib/aws-logs'
import {RemovalPolicy} from 'aws-cdk-lib'
import {Repository} from "aws-cdk-lib/aws-ecr";

export interface TaskDefinitionProperties {
    stackSuffix: string
    applicationRole: IRole
    settings: Settings
}

export class DpaIdAuth0BackendDemoTaskDefinition extends Construct {
    public readonly instance: FargateTaskDefinition

    constructor(scope: Construct, id: string, props: TaskDefinitionProperties) {
        super(scope, id)
        const settings = props.settings.deploymentSettings

        /**
         * Creating role for application.
         */
        const logGroup = new LogGroup(this, 'AppLogGroup', {
            logGroupName: `dpa-id-auth0-backend-demo-${props.stackSuffix}`,
            retention: RetentionDays.SIX_MONTHS,
            removalPolicy: RemovalPolicy.DESTROY
        })


        this.instance = new FargateTaskDefinition(this, 'TaskDefinition', {
            taskRole: props.applicationRole,
            memoryLimitMiB: settings.memoryLimit,
            cpu: settings.cpu
        })
        this.instance.addContainer('Container', {
            image: ContainerImage.fromEcrRepository(Repository.fromRepositoryArn(this, "ecr-repo",
                settings.repositoryArn), settings.imageTag),
            portMappings: [
                {containerPort: settings.applicationPort, protocol: Protocol.TCP}
            ],
            containerName: `dpa-id-auth0-backend-demo`,
            environment: {
                SPRING_PROFILES_ACTIVE: settings.springProfile
            },
            logging: LogDriver.awsLogs({
                streamPrefix: 'backend-demo',
                logGroup
            }),
            essential: true
        })
    }
}
