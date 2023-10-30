import chai, { expect } from "chai";
import chaiHttp from "chai-http";
import app from "../../server";
import sha1 from "sha1";
import dbClient from "../../utils/db";
import redisClient from "../../utils/redis";

chai.use(chaiHttp);

describe("UserController", () => {
  describe("POST /users", () => {
    let testUser, users;

    beforeEach(async () => {
      users = await dbClient.users();
      testUser = {
        email: "test@example.com",
        password: "test_password",
      };
    });
    afterEach(async () => {
      await users.deleteOne({ email: testUser.email });
    });

    it("should add new user to db", async () => {
      const res = await chai.request(app).post("/users").send(testUser);

      expect(res).to.have.status(201);
      expect(res.body).to.have.property("id");
      expect(res.body).to.have.property("email", testUser.email);

      const findUser = await users.findOne({ email: testUser.email });

      expect(findUser).to.exist;
      expect(findUser).to.have.property("email", testUser.email);
      expect(findUser).to.have.property("password");
      expect(findUser.password).to.be.equal(sha1(testUser.password));
    });
    it("should return an error if email is missing", async () => {
      const res = await chai
        .request(app)
        .post("/users")
        .send({ password: testUser.password });

      expect(res).to.have.status(400);
      expect(res.body).to.have.property("error", "Missing email");
    });
    it("should return an error if password is missing", async () => {
      const res = await chai
        .request(app)
        .post("/users")
        .send({ email: testUser.email });

      expect(res).to.have.status(400);
      expect(res.body).to.have.property("error", "Missing password");
    });
    it("should return an error if email already exist", async () => {
      await users.insertOne(testUser);

      const res = await chai.request(app).post("/users").send(testUser);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property("error", "Already exist");
    });
    it("should store password as SHA1", async () => {
      const res = await chai.request(app).post("/users").send(testUser);

      const users = await dbClient.users();
      const user = await users.findOne({ email: res.body.email });

      const hashpass = sha1(testUser.password);
      const notHash = testUser.password === user.password;

      expect(res).to.have.status(201);
      expect(user.password).to.deep.equal(hashpass);
      expect(notHash).to.be.false;
    });
  });

  describe("GET /users/me", () => {
    let userToken;
    let userId;
    let testData;
    let users;
    let key;

    before(async () => {
      users = await dbClient.users();
      testData = {
        email: "testUser@example.com",
        password: sha1("testpassword"),
      };
      await users.insertOne(testData);
      const user = await users.findOne({ email: testData.email });
      userId = user._id.toString();

      const key = `auth_${userId}`;
      await redisClient.set(key, userId, 100);
      userToken = key.replace("auth_", "");
    });

    after(async () => {
      await users.deleteOne(testData);
      await redisClient.del(key);
    });

    it("should return userId and email on valid token", async () => {
      const res = await chai
        .request(app)
        .get("/users/me")
        .set("X-Token", userToken);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("id", userId);
      expect(res.body).to.have.property("email", testData.email);
    });
    it("should return an error if the user is not authenticated", async () => {
      const res = await chai
        .request(app)
        .get("/users/me")
        .set("X-Token", "invalid-token");

      expect(res).to.have.status(401);
      expect(res.body).to.be.an("object");
      expect(res.body).to.have.property("error", "Unauthorized");
    });
  });
});
