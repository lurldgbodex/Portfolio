import { createClient } from "redis";

class RedisClient {
  constructor() {
    this.client = createClient();
    this.client.connect();

    this.client.on("error", (err) =>
      console.log(`Redis Client Connection Failed: ${err}`)
    );
    this.client.on("connect", () => console.log(`Redis Client connected`));
  }

  async isAlive() {
    try {
      const active = await this.client.ping();
      return Boolean(active);
    } catch (err) {
      console.log(`Redis check connection ${err}`);
      return false;
    }
  }

  async get(key) {
    try {
      const value = await this.client.get(key);
      return value;
    } catch (err) {
      console.log(`Redis get Value ${err}`);
      return null;
    }
  }

  async set(key, value, duration) {
    try {
      await this.client.set(key, value, { EX: duration });
      return true;
    } catch (err) {
      console.error(`Redis set value ${err}`);
      return false;
    }
  }

  async del(key) {
    try {
      await this.client.del(key);
      return true;
    } catch (err) {
      console.error(`Redis del key ${err}`);
      return false;
    }
  }

  async disconnect() {
    try {
      await this.client.disconnect();
      return true;
    } catch (err) {
      console.error(`Unable to disconnect redis client: ${err}`);
      return false;
    }
  }
}

const redisClient = new RedisClient();

module.exports = redisClient;
