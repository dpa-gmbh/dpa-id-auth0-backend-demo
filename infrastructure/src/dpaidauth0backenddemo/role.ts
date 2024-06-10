/**
 * The DpaIdAuth0BackendDemo Role defines the permissions of the DpaIdAuth0BackendDemo component, e.g. any
 * requests it may perform against some AWS infrastructure.
 */
import {
    Effect,
    IRole,
    PolicyStatement,
    Role,
    ServicePrincipal,
} from "aws-cdk-lib/aws-iam";
import {Construct} from "constructs";
import {Stack} from "aws-cdk-lib";

export class DpaIdAuth0BackendDemoRole {
    public readonly instance: IRole;

    constructor(scope: Construct) {
        const role = new Role(scope, "DpaIdAuth0BackendDemo-TaskRole", {
            assumedBy: new ServicePrincipal("ecs-tasks.amazonaws.com"),
            description:
                "Task Role of the dpa Id auth0 backend Application Container. This might be used if the container accesses and AWS services",
        });

        DpaIdAuth0BackendDemoRole.allowAccessToParameterStore(role);
        this.instance = role;
    }

    private static allowAccessToParameterStore(role: Role) {
        const parentStack = Stack.of(role);
        const region = parentStack.region;
        const account = parentStack.account;

        role.addToPolicy(
            new PolicyStatement({
                resources: [
                    `arn:aws:ssm:${region}:${account}:parameter/config/dpa-id-auth0-backend-demo*`,
                    `arn:aws:ssm:${region}:${account}:parameter/config/application*`,
                ],
                actions: ["ssm:GetParametersByPath"],
                effect: Effect.ALLOW,
            })
        );
    }
}
