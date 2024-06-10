import {Construct} from 'constructs'
import {ILoadBalancerV2} from 'aws-cdk-lib/aws-elasticloadbalancingv2'
import {
    ARecord,
    IHostedZone,
    RecordTarget
} from 'aws-cdk-lib/aws-route53'
import {LoadBalancerTarget} from 'aws-cdk-lib/aws-route53-targets'

export interface DpaIdAuth0BackendDemoDnsRecordProperties {
    zone: IHostedZone
    recordName: string
    loadBalancer: ILoadBalancerV2
}

/**
 * DNS Record under which the application shall be available.
 */
export class DpaIdAuth0BackendDemoDnsRecord {
    constructor(scope: Construct, props: DpaIdAuth0BackendDemoDnsRecordProperties) {
        const target = new LoadBalancerTarget(props.loadBalancer)
        const {recordName, zone} = props
        new ARecord(scope, 'ApplicationDnsARecord', {
            recordName,
            zone,
            target: RecordTarget.fromAlias(target)
        })
    }
}
