import { expect } from "chai";
import dbClient from "../../utils/db";

describe("MongoDB Databse", () => {
  describe("nbUsers method", () => {
    it("should return no of users in users collection", async () => {
      const nbUsers = await dbClient.nbUsers();
      expect(nbUsers).to.be.a("number");
    });
  });
  describe("nbFiles method", () => {
    it("should return the no of files in the files collection", async () => {
      const nbFiles = await dbClient.nbFiles();
      expect(nbFiles).to.be.a("number");
    });
  });
  describe("isAlive method", () => {
    it("should return true if db is connected", async () => {
      const res = await dbClient.isAlive();
      expect(res).to.be.true;
    });
    it("should return false if Db connection fails", async () => {
      await dbClient.quit();
      const res = await dbClient.isAlive();
      expect(res).to.be.false;
    });
  });
});
