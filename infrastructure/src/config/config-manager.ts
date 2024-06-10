import { Configuration } from "./configuration";
import {MasterConfig} from "./stages/master";

export class ConfigManager {
  static createConfig(stage: string): Configuration {
    // eslint-disable-next-line no-console
    console.log(`Creating configuration for stage ${stage}`);

    switch (stage) {
      case MasterConfig.STAGE:
        return new MasterConfig();
      default:
        throw new Error(`Unknown stage ${stage}`);
    }
  }
}
