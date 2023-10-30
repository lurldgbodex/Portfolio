const chai = require("chai");
const chaiHttp = require("chai-http");
const dbClient = require("../../utils/db");
const redisClient = require("../../utils/redis");
const app = require("../../server");
const sha1 = require("sha1");
const { v4: uuidv4 } = require("uuid");

chai.use(chaiHttp);
const expect = chai.expect;

describe("AuthController", () => {
  describe("GET /connect", () => {
    it("should return a token on succesful authentication", async () => {
      const email = "test@example.com";
      const password = "password123";
      const hashedPassword = sha1(password);
      const user = {
        email,
        password: hashedPassword,
      };
      const users = await dbClient.users();
      await users.insertOne(user);

      const credentials = Buffer.from(`${email}:${password}`).toString(
        "base64"
      );
      const res = await chai
        .request(app)
        .get("/connect")
        .set("Authorization", `Basic ${credentials}`);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("token");
      const token = res.body.token;
      const key = `auth_${token}`;
      const userId = await redisClient.get(key);
      expect(userId).to.equal(user._id.toString());

      await users.deleteOne({ _id: user._id });
    });

    it("should return an error for invalid email or password", async () => {
      const email = "test@example.com";
      const password = "password123";
      const res = await chai
        .request(app)
        .get("/connect")
        .auth(email, password, { type: "basic" });

      expect(res).to.have.status(401);
      expect(res.body).to.have.property("error");
      expect(res.body.error).to.equal("Unauthorized");
    });
  });

  describe("GET /disconnect", () => {
    it("should disconnect a user and delete token", async function () {
      const userId = "test-user-id";
      const token = uuidv4();
      const key = `auth_${token}`;
      await redisClient.set(key, userId, 10);

      const res = await chai
        .request(app)
        .get("/disconnect")
        .set("X-Token", token);

      expect(res).to.have.status(204);
      const userIdAfterDisconnet = await redisClient.get(key);
      expect(userIdAfterDisconnet).to.be.null;
    });

    it("should return an error for invalid token", async () => {
      const res = await chai
        .request(app)
        .get("/disconnect")
        .set("X-Token", "invalid-token");

      expect(res).to.have.status(401);
      expect(res.body).to.have.property("error");
      expect(res.body.error).to.equal("Unauthorized");
    });
  });
});
