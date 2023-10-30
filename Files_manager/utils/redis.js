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
      return false;
    }
  }

  async get(key) {
    try {
      const value = await this.client.get(key);
      return value;
    } catch (err) {
      return null;
    }
  }

  async set(key, value, duration) {
    try {
      await this.client.set(key, value, { EX: duration });
      return true;
    } catch (err) {
      return false;
    }
  }

  async del(key) {
    try {
      await this.client.del(key);
      return true;
    } catch (err) {
      return false;
    }
  }

  async disconnect() {
    try {
      await this.client.disconnect();
      return true;
    } catch (err) {
      return false;
    }
  }

  async flushall() {
    try {
      await this.client.flushAll();
      return true;
    } catch (err) {
      return false;
    }
  }
}

const redisClient = new RedisClient();

module.exports = redisClient;
