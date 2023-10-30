import { assert, expect } from "chai";
import redisClient from "../../utils/redis";

describe("RedisClient", () => {
  afterEach(async () => {
    try {
      await redisClient.del("test-key");
    } catch (err) {
      console.error(err);
    }
  });

  describe("Redis set method", () => {
    it("should set a value to a key with an expiration date", async () => {
      const test_key = "test-key";
      const test_value = "12";
      const duration = 10;
      const res = await redisClient.set(test_key, test_value, duration);

      expect(res).to.be.true;
    });
  });

  describe("Redis get method", () => {
    it("should retrieve a value set in redis", async () => {
      const test_key = "test_key";
      const test_value = "value";
      const duration = 10;
      await redisClient.set(test_key, test_value, duration);

      const val = await redisClient.get(test_key);

      expect(val).to.be.equal(test_value);
    });
    it("should return null if key doesn't exists", async () => {
      const val = await redisClient.get("key");
      expect(val).to.be.null;
    });
    it("should return null if set expired", async () => {
      const test_key = "test_key";
      const test_value = "test_value";
      const duration = 5;
      await redisClient.set(test_key, test_value, duration);
      const val = await redisClient.get(test_key);

      expect(val).to.be.equal(test_value);

      setTimeout(async () => {
        const valAfterEx = await redisClient.get(test_key);
        expect(valAfterEx).to.be.null;
      }, 1000 * 10);
    });
  });
  describe("Redis delete Method", () => {
    it("should delete key and return true if successful", async () => {
      await redisClient.set("test_key", "12", 10);
      const val = await redisClient.get("test_key");
      expect(val).to.be.equal("12");
      const res = await redisClient.del("test_key");
      expect(res).to.be.true;
      const valAfterDelete = await redisClient.get("test_key");
      expect(valAfterDelete).to.be.null;
    });
  });

  describe("Redis flushall()", () => {
    it("should return true if succussful", async () => {
      const res = await redisClient.flushall();
      expect(res).to.be.true;
    });
  });

  describe("Redis connection isAlive", () => {
    it("should return true if redis client is alive", async () => {
      const isAlive = await redisClient.isAlive();
      expect(isAlive).to.be.true;
    });
    it("should return false if redis is not alive", async () => {
      await redisClient.disconnect();
      const isAlive = await redisClient.isAlive();
      expect(isAlive).to.be.false;
    });
  });
});
