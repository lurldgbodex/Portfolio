import chai, { expect } from "chai";
import chaiHttp from "chai-http";

import app from "../../server";

chai.use(chaiHttp);

describe("App Controller", () => {
  describe("GET /status", () => {
    it("should return redis and db status", async () => {
      const res = await chai.request(app).get("/status");
      expect(res).to.have.status(200);
      expect(res.body).to.deep.equal({ redis: true, db: true });
    });
  });
  describe("GET /stats", () => {
    it("should return the number of users and files in db", async () => {
      const res = await chai.request(app).get("/stats");
      expect(res).to.have.status(200);
      expect(res.body).to.have.property("users");
      expect(res.body).to.have.property("files");
    });
  });
});
