import { Duration, Environment } from "aws-cdk-lib";

export interface Configuration {
  stage: string;
  env: Environment;
  settings: Settings;
}

export interface Settings {
  vpc: {
    name: string;
  };
  ecsClusterSettings: ECSClusterSettings;
  loadbalancerSettings: LoadbalancerSettings;
  dnsSettings: DnsSettings;
  deploymentSettings: DeploymentSettings;
  identifier?: string;
}

export interface DnsSettings {
  hostedZoneName: string;
  hostedZoneId: string
  recordName: string;
}

export interface ECSClusterSettings {
  arn: string;
  name: string;
}

export interface LoadbalancerSettings {
  securityGroupId: string;
  loadbalancerArn: string;
  loadbalancerCanonicalHostedZoneId: string;
  loadbalancerDnsName: string;
  loadbalancerListenerArn: string;
}

export interface DeploymentSettings {
  repositoryArn: string;
  deregistrationDelay: Duration;
  applicationPort: number;
  lbPriority: number;
  imageTag?: string;
  memoryLimit: number,
  cpu: number,
  springProfile: string,
  stageSuffix: string;
  environment: {
    [key: string]: string;
  };
}
