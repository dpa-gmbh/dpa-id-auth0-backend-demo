#!/usr/bin/env node
import "source-map-support/register";
import * as cdk from "aws-cdk-lib";
import { DpaIdAuth0BackendDemoStack } from "../src/dpaidauth0backenddemo";
import { Configuration } from "../src/config/configuration";
import { ConfigManager } from "../src/config/config-manager";

const app = new cdk.App();

let stage = process.env.STAGE || null;
console.log("Stage: "+stage)
if (!stage) throw new Error("STAGE is not set");

const config: Configuration = ConfigManager.createConfig(stage);

const dpaIdAuth0BackendDemoStack = new DpaIdAuth0BackendDemoStack(
    app,
    "dpa-id-auth0-backend-demo",
    {
        env: config.env,
        settings: config.settings,
        stackSuffix: stage
    }
);