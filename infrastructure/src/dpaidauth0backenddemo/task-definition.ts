import {Construct} from 'constructs'
import {IRole} from 'aws-cdk-lib/aws-iam'
import {
    ContainerImage,
    FargateTaskDefinition,
    LogDriver,
    Protocol,
    Secret as ECSSecret,
    ContainerDependencyCondition,
} from 'aws-cdk-lib/aws-ecs'
import {Settings} from '../config/configuration'
import {LogGroup, RetentionDays} from 'aws-cdk-lib/aws-logs'
import {RemovalPolicy} from 'aws-cdk-lib'
import {Repository} from "aws-cdk-lib/aws-ecr";
import { Secret } from "aws-cdk-lib/aws-secretsmanager";
import * as ssm from 'aws-cdk-lib/aws-ssm';

export interface TaskDefinitionProperties {
    stackSuffix: string
    applicationRole: IRole
    settings: Settings
}

export class DpaIdAuth0BackendDemoTaskDefinition extends Construct {
    public readonly instance: FargateTaskDefinition
    private readonly laceworkMemoryPercentage = 10;

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

        let clientSecret = this.getParameterFromSSM("client_secret");

        const appContainer = this.instance.addContainer('Container', {
            image: ContainerImage.fromEcrRepository(Repository.fromRepositoryArn(this, "ecr-repo",
                settings.repositoryArn), settings.imageTag),
            portMappings: [
                {containerPort: settings.applicationPort, protocol: Protocol.TCP}
            ],
            containerName: `dpa-id-auth0-backend-demo`,
            environment: {
                SPRING_PROFILES_ACTIVE: settings.springProfile,
                CLIENT_SECRET: clientSecret,
                LaceworkServerUrl: "https://agent.euprodn.lacework.net",
                LaceworkConfig: `{"memlimit":"${Math.floor(
                    (settings.memoryLimit * this.laceworkMemoryPercentage) / 100
                )}M"}`,
            },
            logging: LogDriver.awsLogs({
                streamPrefix: 'backend-demo',
                logGroup
            }),
            essential: true,
            secrets: {
                LaceworkAccessToken: ECSSecret.fromSecretsManager(
                    Secret.fromSecretNameV2(
                        this,
                        "LaceworkAccessToken",
                        "LaceworkAccessToken"
                    )
                ),
            },
            entryPoint: ["/var/lib/lacework-backup/lacework-sidecar.sh", "/usr/local/bin/start-service.sh"],
        })

        const laceworkContainer = this.instance.addContainer(
            "lacework-collector",
            {
                containerName: `lacework-collector`,
                image: ContainerImage.fromEcrRepository(
                    Repository.fromRepositoryArn(
                        this,
                        "LaceworkRepositoy",
                        "arn:aws:ecr:eu-central-1:478324715856:repository/lacework/datacollector"
                    ),
                    "latest-sidecar"
                ),
                logging: LogDriver.awsLogs({
                    streamPrefix: `lacework-collector`,
                    logRetention: RetentionDays.ONE_MONTH,
                }),
                essential: false,
            }
        );

        appContainer.addVolumesFrom({
            readOnly: true,
            sourceContainer: `lacework-collector`,
        });

        appContainer.addContainerDependencies({
            container: laceworkContainer,
            condition: ContainerDependencyCondition.SUCCESS,
        });
    }
    private getParameterFromSSM(suffix: string) {

        return ssm.StringParameter.valueForStringParameter(this, `/config/dpa-id-auth0-backend-demo/${suffix}`);
    }
}
