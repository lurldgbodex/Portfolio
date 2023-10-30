import chai from "chai";
import chaiHttp from "chai-http";
import { ObjectID } from "mongodb";
import redisClient from "../../utils/redis";
import dbClient from "../../utils/db";
import app from "../../server";

chai.use(chaiHttp);
const expect = chai.expect;

describe("FilesController", () => {
  describe("POST /files", () => {
    let userId;
    let token;

    before(async () => {
      await redisClient.flushall();

      const testUser = {
        _id: new ObjectID(),
        email: "testUser@example.com",
        password: "password",
      };
      const rediskey = `auth_${testUser._id.toString()}`;
      await redisClient.set(rediskey, testUser._id.toString(), 24 * 60 * 60);
      userId = testUser._id.toString();
      token = testUser._id.toString();
    });

    after(async () => {
      const files = await dbClient.files();
      await files.deleteMany({ userId });
      await redisClient.flushall();
    });

    it("should return 401 if invalid token user", async () => {
      const fileData = {
        name: "test-file.txt",
        type: "folder",
      };

      const res = await chai
        .request(app)
        .post("/files")
        .set("x-token", "invalid_token")
        .send(fileData);

      expect(res).to.have.status(401);
      expect(res.body).to.have.property("error");
      expect(res.body.error).to.equal("Unauthorized");
    });

    it("should create a new folder at the root", async () => {
      const folderData = {
        name: "Test Folder",
        type: "folder",
      };
      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(folderData);
      expect(res).to.have.status(201);
      expect(res.body).to.have.property("_id");
      expect(res.body.name).to.equal(folderData.name);
      expect(res.body.type).to.equal(folderData.type);
      expect(res.body.parentId).to.equal(0);
      expect(res.body.isPublic).to.equal(false);
      expect(res.body.localPath).to.be.undefined;
    });

    it("should create a new folder inside a folder", async () => {
      const folderData = {
        name: "parent test Folder",
        type: "folder",
      };
      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(folderData);
      const parentId = res.body._id;
      const parentType = res.body.type;

      const folderData2 = {
        name: "child test folder",
        type: "folder",
        parentId: parentId,
      };

      const res2 = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(folderData2);

      expect(res2).to.have.status(201);
      expect(res2.body.name).to.deep.equal(folderData2.name);
      expect(res2.body.type).to.deep.equal(folderData2.type);
      expect(res2.body.parentId).to.deep.equal(parentId);
      expect(parentType).to.deep.equal(folderData.type);
    });

    it("should create a new file at the root", async () => {
      const fileName = "test.txt";
      const fileData = "VGhpcyBpcyBhIHRlc3QgZmlsZQ=="; // base64-encoded string 'This is a test file'
      const fileMetaData = {
        name: fileName,
        type: "file",
        parentId: 0,
        isPublic: true,
        data: fileData,
      };

      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileMetaData);

      expect(res).to.have.status(201);
      expect(res.body).to.have.property("_id");
      expect(res.body.name).to.equal(fileMetaData.name);
      expect(res.body.type).to.equal(fileMetaData.type);
      expect(res.body.parentId).to.equal(fileMetaData.parentId);
      expect(res.body.isPublic).to.be.true;
      expect(res.body.localPath).to.be.a("string");
    });

    it("should create a file inside of a folder", async () => {
      const folderData = {
        name: "parent test Folder",
        type: "folder",
      };
      const resFolder = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(folderData);

      const fileName = "test.txt";
      const fileData = "VGhpcyBpcyBhIHRlc3QgZmlsZQ=="; // base64-encoded string 'This is a test file'
      const fileMetaData = {
        name: fileName,
        type: "file",
        parentId: resFolder.body.parentId,
        isPublic: true,
        data: fileData,
      };

      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileMetaData);

      expect(res).to.have.status(201);
      expect(res.body).to.have.property("_id");
      expect(res.body.name).to.equal(fileMetaData.name);
      expect(res.body.type).to.equal(fileMetaData.type);
      expect(res.body.parentId).to.equal(resFolder.body.parentId);
      expect(res.body.isPublic).to.be.true;
      expect(res.body.localPath).to.be.a("string");
    });

    it("should return 400 if name is missing", async () => {
      const fileData = "VGhpcyBpcyBhIHRlc3QgZmlsZQ=="; // base64-encoded string 'This is a test file'
      const fileMetaData = {
        type: "file",
        parentId: 0,
        isPublic: true,
        data: fileData,
      };

      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileMetaData);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property("error");
      expect(res.body.error).to.equal("Missing name");
    });

    it("should return 400 if type is missing", async () => {
      const fileData = "VGhpcyBpcyBhIHRlc3QgZmlsZQ=="; // base64-encoded string 'This is a test file'
      const fileMetaData = {
        name: "test-file",
        parentId: 0,
        isPublic: true,
        data: fileData,
      };

      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileMetaData);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property("error");
      expect(res.body.error).to.equal("Missing type");
    });

    it("should return 400 if no data and type not folder", async () => {
      const fileMetaData = {
        name: "test-file.txt",
        type: "file",
        parentId: 0,
        isPublic: true,
      };

      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileMetaData);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property("error");
      expect(res.body.error).to.equal("Missing data");
    });

    it("should return 400 if parent is not found", async () => {
      const fileMetaData = {
        name: "test folder",
        type: "folder",
        parentId: new ObjectID(),
      };
      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileMetaData);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property("error", "Parent not found");
    });

    it("should return 400 if parent not a folder", async () => {
      const parent = {
        userId: ObjectID(),
        name: "parent folder",
        type: "file",
      };
      const files = await dbClient.files();
      const parentFolder = await files.insertOne(parent);
      const fileData = "VGhpcyBpcyBhIHRlc3QgZmlsZQ==";
      const fileMetaData = {
        name: "test-file",
        type: "file",
        parentId: parentFolder.ops[0]._id,
        data: fileData,
      };
      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileMetaData);

      expect(res).to.have.status(400);
      expect(res.body).to.have.property("error", "Parent is not a folder");
      await files.deleteOne({ _id: parentFolder._id });
    });
  });

  describe("GET /files/:id", () => {
    let token;
    let fileId;
    let fileMetaData;

    before(async () => {
      await redisClient.flushall();

      const testUser = {
        _id: new ObjectID(),
        email: "testUser@example.com",
        password: "password",
      };
      const rediskey = `auth_${testUser._id.toString()}`;
      await redisClient.set(rediskey, testUser._id.toString(), 24 * 60 * 60);
      token = testUser._id.toString();

      const fileData = "VGhpcyBpcyBhIHRlc3QgZmlsZQ==";
      fileMetaData = {
        name: "test-file",
        type: "file",
        parentId: 0,
        isPublic: false,
        data: fileData,
      };
      const res = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileMetaData);
      fileId = res.body._id;
    });

    after(async () => {
      const files = await dbClient.files();
      await files.deleteOne({ _id: ObjectID(fileId) });
      await redisClient.flushall();
    });

    it("should return the details of the specified file", async () => {
      const res = await chai
        .request(app)
        .get(`/files/${fileId}`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("_id", fileId);
      expect(res.body).to.have.property("name", fileMetaData.name);
      expect(res.body).to.have.property("type", fileMetaData.type);
      expect(res.body).to.have.property("parentId", fileMetaData.parentId);
      expect(res.body).to.have.property("isPublic", fileMetaData.isPublic);
      expect(res.body).to.have.property("userId");
    });

    it("should return 401 if user is not authenticated", async () => {
      const res = await chai.request(app).get(`/files/${fileId}`);

      expect(res).to.have.status(401);
      expect(res.body).to.have.property("error", "Unauthorized");
    });

    it("should return 404 if file is not found", async () => {
      const invalidFileId = new ObjectID().toString();
      const res = await chai
        .request(app)
        .get(`/files/${invalidFileId}`)
        .set("X-Token", token);

      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
  });

  describe("GET /files", () => {
    let token;
    before(async () => {
      try {
        await redisClient.flushall();

        const testUser = {
          _id: new ObjectID(),
          email: "testUser@example.com",
          password: "password",
        };
        const rediskey = `auth_${testUser._id.toString()}`;
        await redisClient.set(rediskey, testUser._id.toString(), 24 * 60 * 60);
        token = testUser._id.toString();
      } catch (error) {
        console.error(error);
      }
    });

    after(async () => {
      await redisClient.flushall();
    });

    it("should return 401 if user is not authenticated", async () => {
      const res = await chai.request(app).get("/files");

      expect(res).to.have.status(401);
      expect(res.body).to.have.property("error", "Unauthorized");
    });

    it("should return a list of files if user is authenticated", async () => {
      const res = await chai.request(app).get("/files").set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.be.an("array");
    });

    it("should return a list of files for a specified parent folder", async () => {
      // create a new folder
      const folderData = {
        name: "test-folder",
        type: "folder",
        parentId: 0,
      };

      const res1 = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(folderData);
      const folderId = res1.body._id;

      // create a new file in folder
      const data = "VGhpcyBpcyBhIHRlc3QgZmlsZQ==";
      const fileData = {
        name: "test-file",
        type: "file",
        parentId: folderId,
        data: data,
      };

      const res2 = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileData);
      const fileId = res2.body._id;

      // get list of files for the parent folder
      const res3 = await chai
        .request(app)
        .get(`/files?parentId=${folderId}`)
        .set("X-Token", token);

      expect(res3).to.have.status(200);
      expect(res3.body).to.be.an("array").with.lengthOf(1);
      expect(res3.body[0]).to.have.property("name", fileData.name);
      expect(res3.body[0]).to.have.property("type", fileData.type);
      expect(res3.body[0]).to.have.property("parentId", fileData.parentId);
      expect(res3.body[0]).to.have.property("isPublic", false);
      expect(res3.body[0]).to.have.property("userId");
      expect(res3.body[0]).to.have.property("localPath");
      expect(res3.body[0]).to.have.property("_id", fileId);

      // delete folder and file
      await chai.request(app).delete(`/files/${fileId}`).set("X-Token", token);
      await chai
        .request(app)
        .delete(`/files/${folderId}`)
        .set("X-Token", token);
    });
  });

  describe("PUT /files/:id/publish", () => {
    let token, userId, fileId, token2, userId2, fileId2;

    beforeEach(async () => {
      // user datas
      const userData1 = {
        email: "testUser184795@email.com",
        password: "password123",
      };

      const userData2 = {
        email: "dkdkjg8i@testmail.com",
        password: "justpassword2",
      };

      // create two users and get the user ids
      const res = await chai.request(app).post("/users").send(userData1);
      expect(res).to.have.status(201);

      userId = res.body.id;

      const res2 = await chai.request(app).post("/users").send(userData2);
      expect(res2).to.have.status(201);

      userId2 = res2.body.id;

      // get user 1 tokens
      const authString = `${userData1.email}:${userData1.password}`;
      const base64Auth = Buffer.from(authString).toString("base64");
      const basicAuthToken = `Basic ${base64Auth}`;

      const getToken = await chai
        .request(app)
        .get("/connect")
        .set("Authorization", basicAuthToken);
      expect(getToken).to.have.status(200);
      token = getToken.body.token;

      // get token for user 2
      const authString2 = `${userData2.email}:${userData2.password}`;
      const base64Auth2 = Buffer.from(authString2).toString("base64");
      const basicAuthToken2 = `Basic ${base64Auth2}`;

      const getUser2Token = await chai
        .request(app)
        .get("/connect")
        .set("Authorization", basicAuthToken2);
      expect(getUser2Token).to.have.status(200);
      token2 = getUser2Token.body.token;

      // create file for user 1
      const fileData = {
        name: "test-file.txt",
        type: "file",
        data: "some text in the file",
      };
      const createFileRes = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileData);

      expect(createFileRes).to.have.status(201);
      expect(createFileRes.body).to.have.property("_id");
      fileId = createFileRes.body._id;

      // create file for user 2
      const fileData2 = {
        name: "test-file2.txt",
        type: "file",
        data: "data for text file 2",
      };
      const createFileRes2 = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token2)
        .send(fileData2);

      expect(createFileRes2).to.have.status(201);
      expect(createFileRes2.body).to.have.property("_id");
      fileId2 = createFileRes2.body._id;
    });

    afterEach(async () => {
      //flush redisclient
      await redisClient.flushall();

      //dispose of test user in users collection
      const users = await dbClient.users();
      const user = await users.findOne({ _id: ObjectID(userId) });
      const user2 = await users.findOne({ _id: ObjectID(userId2) });

      await users.deleteOne({ _id: user._id });
      await users.deleteOne({ _id: user2._id });

      //dispose of test files in files collection
      const files = await dbClient.files();
      const file = await files.findOne({ _id: ObjectID(fileId) });

      const file2 = await files.findOne({ _id: ObjectID(fileId2) });
      await files.deleteOne({ _id: file._id });
      await files.deleteOne({ _id: file2._id });
    });

    it("should return 401 error for invalid token user", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/publish`)
        .set("X-Token", "invalid Token");

      expect(res).to.have.status(401);
      expect(res.body).to.have.property("error", "Unauthorized");
    });
    it("should return 404 error if no file linked to :id", async () => {
      const someId = new ObjectID();
      const res = await chai
        .request(app)
        .put(`/files/${someId}/publish`)
        .set("X-Token", token);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should return 404 error if no file linked to :id for user", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId2}/publish`)
        .set("X-Token", token);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should publish file with correct :id of the owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/publish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", true);
    });
  });
  describe("PUT /files/:id/unpublish", () => {
    let token, userId, fileId, token2, userId2, fileId2;

    beforeEach(async () => {
      // user datas
      const userData1 = {
        email: "testUser184795@email.com",
        password: "password123",
      };

      const userData2 = {
        email: "dkdkjg8i@testmail.com",
        password: "justpassword2",
      };

      // create two users and get the user ids
      const res = await chai.request(app).post("/users").send(userData1);
      expect(res).to.have.status(201);

      userId = res.body.id;

      const res2 = await chai.request(app).post("/users").send(userData2);
      expect(res2).to.have.status(201);

      userId2 = res2.body.id;

      // get user 1 tokens
      const authString = `${userData1.email}:${userData1.password}`;
      const base64Auth = Buffer.from(authString).toString("base64");
      const basicAuthToken = `Basic ${base64Auth}`;

      const getToken = await chai
        .request(app)
        .get("/connect")
        .set("Authorization", basicAuthToken);
      expect(getToken).to.have.status(200);
      token = getToken.body.token;

      // get token for user 2
      const authString2 = `${userData2.email}:${userData2.password}`;
      const base64Auth2 = Buffer.from(authString2).toString("base64");
      const basicAuthToken2 = `Basic ${base64Auth2}`;

      const getUser2Token = await chai
        .request(app)
        .get("/connect")
        .set("Authorization", basicAuthToken2);
      expect(getUser2Token).to.have.status(200);
      token2 = getUser2Token.body.token;

      // create file for user 1
      const fileData = {
        name: "test-file.txt",
        type: "file",
        isPublic: true,
        data: "SGVsbG8gV2Vic3RhY2shCg==",
      };
      const createFileRes = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileData);

      expect(createFileRes).to.have.status(201);
      expect(createFileRes.body).to.have.property("_id");
      fileId = createFileRes.body._id;

      // create file for user 2
      const fileData2 = {
        name: "test-file2.txt",
        type: "file",
        isPublic: true,
        data: "SGVsbG8gV2Vic3RhY2shCg==",
      };
      const createFileRes2 = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token2)
        .send(fileData2);

      expect(createFileRes2).to.have.status(201);
      expect(createFileRes2.body).to.have.property("_id");
      fileId2 = createFileRes2.body._id;
    });

    afterEach(async () => {
      //flush redisclient
      await redisClient.flushall();

      //dispose of test user in users collection
      const users = await dbClient.users();
      const user = await users.findOne({ _id: ObjectID(userId) });
      const user2 = await users.findOne({ _id: ObjectID(userId2) });

      await users.deleteOne({ _id: user._id });
      await users.deleteOne({ _id: user2._id });

      //dispose of test files in files collection
      const files = await dbClient.files();
      const file = await files.findOne({ _id: ObjectID(fileId) });

      const file2 = await files.findOne({ _id: ObjectID(fileId2) });
      await files.deleteOne({ _id: file._id });
      await files.deleteOne({ _id: file2._id });
    });
    it("should return 401 error for invalid token user", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/unpublish`)
        .set("X-Token", "invalid Token");

      expect(res).to.have.status(401);
      expect(res.body).to.have.property("error", "Unauthorized");
    });
    it("should return 404 error if no file is linked to :id", async () => {
      const someId = new ObjectID();
      const res = await chai
        .request(app)
        .put(`/files/${someId}/publish`)
        .set("X-Token", token);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should return 404 error if no file linked to :id for user", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId2}/unpublish`)
        .set("X-Token", token);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should unpublish files with correct :id of the owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/unpublish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", false);
    });
  });

  describe("GET /files/:id/data", () => {
    let token, userId, fileId, token2, userId2, fileId2, folderId;
    let fileIdpublishNoLocal;
    let fileIdUnpublishNoLocal;
    beforeEach(async () => {
      // user datas
      const userData1 = {
        email: "testUser184795@email.com",
        password: "password123",
      };

      const userData2 = {
        email: "dkdkjg8i@testmail.com",
        password: "justpassword2",
      };

      // create two users and get the user ids
      const res = await chai.request(app).post("/users").send(userData1);
      expect(res).to.have.status(201);

      userId = res.body.id;

      const res2 = await chai.request(app).post("/users").send(userData2);
      expect(res2).to.have.status(201);

      userId2 = res2.body.id;

      // get user 1 tokens
      const authString = `${userData1.email}:${userData1.password}`;
      const base64Auth = Buffer.from(authString).toString("base64");
      const basicAuthToken = `Basic ${base64Auth}`;

      const getToken = await chai
        .request(app)
        .get("/connect")
        .set("Authorization", basicAuthToken);
      expect(getToken).to.have.status(200);
      token = getToken.body.token;

      // get token for user 2
      const authString2 = `${userData2.email}:${userData2.password}`;
      const base64Auth2 = Buffer.from(authString2).toString("base64");
      const basicAuthToken2 = `Basic ${base64Auth2}`;

      const getUser2Token = await chai
        .request(app)
        .get("/connect")
        .set("Authorization", basicAuthToken2);
      expect(getUser2Token).to.have.status(200);
      token2 = getUser2Token.body.token;

      // create a folder
      const folderData = {
        name: "testfolder.txt",
        type: "folder",
      };

      const createFolder = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(folderData);

      folderId = createFolder.body._id;

      // create file for user 1
      const fileData = {
        name: "testfile.txt",
        type: "file",
        data: "SGVsbG8gV2Vic3RhY2shCg==",
      };
      const createFileRes = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token)
        .send(fileData);

      expect(createFileRes).to.have.status(201);
      expect(createFileRes.body).to.have.property("_id");
      fileId = createFileRes.body._id;

      // create file for user 2
      const fileData2 = {
        name: "testfile2.txt",
        type: "file",
        data: "SGVsbG8gV2Vic3RhY2shCg==",
      };
      const createFileRes2 = await chai
        .request(app)
        .post("/files")
        .set("X-Token", token2)
        .send(fileData2);

      expect(createFileRes2).to.have.status(201);
      expect(createFileRes2.body).to.have.property("_id");
      fileId2 = createFileRes2.body._id;

      //create publish file without localpath
      const publishFileNoLocal = {
        userId: ObjectID(userId),
        name: "someRandomFile",
        type: "file",
        parentId: "0",
        isPublic: true,
      };

      const files = await dbClient.files();

      const createFilePublish = await files.insertOne(publishFileNoLocal);

      if (createFilePublish && createFilePublish.ops.length > 0) {
        fileIdpublishNoLocal = createFilePublish.ops[0]._id.toString();
      }

      const UnpublishFileNoLocal = {
        userId: ObjectID(userId),
        name: "someRandomFile",
        type: "file",
        parentId: "0",
        isPublic: false,
      };

      const createFileUnPublish = await files.insertOne(UnpublishFileNoLocal);

      if (createFileUnPublish && createFileUnPublish.ops.length > 0) {
        fileIdUnpublishNoLocal = createFileUnPublish.ops[0]._id.toString();
      }
    });

    afterEach(async () => {
      //flush redisclient
      await redisClient.flushall();

      //dispose of test user in users collection
      const users = await dbClient.users();
      const user = await users.findOne({ _id: ObjectID(userId) });
      const user2 = await users.findOne({ _id: ObjectID(userId2) });

      await users.deleteOne({ _id: user._id });
      await users.deleteOne({ _id: user2._id });

      //dispose of test files and folders in files collection
      const files = await dbClient.files();
      const file = await files.findOne({ _id: ObjectID(fileId) });

      const file2 = await files.findOne({ _id: ObjectID(fileId2) });

      const folder = await files.findOne({ _id: ObjectID(folderId) });
      await files.deleteOne({ _id: file._id });
      await files.deleteOne({ _id: file2._id });
      await files.deleteOne({ _id: folder._id });

      if (fileIdpublishNoLocal) {
        await files.deleteOne({ _id: ObjectID(fileIdpublishNoLocal) });
      }
      if (fileIdUnpublishNoLocal) {
        await files.deleteOne({ _id: ObjectID(fileIdUnpublishNoLocal) });
      }
    });
    it("should return 404 error if no file linked to :id", async () => {
      const res = await chai
        .request(app)
        .get(`/files/${new ObjectID()}/data`)
        .set("X-Token", token);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should return 404 error for unpublished file linked to :id but user unauthenticated", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/unpublish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", false);

      const resData = await chai.request(app).get(`/files/${fileId}/data`);
      expect(resData).to.have.status(404);
      expect(resData.body).to.have.property("error", "Not found");
    });

    it("should return 404 error for unplublished file linked to :id, authenticated user but not owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/unpublish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", false);

      const resData = await chai
        .request(app)
        .get(`/files/${fileId}/data`)
        .set("X-Token", token2);

      expect(resData).to.have.status(404);
      expect(resData.body).to.have.property("error", "Not found");
    });
    it("should return data for unpublished file linked to :id and user authenticated and owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/unpublish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", false);

      const resData = await chai
        .request(app)
        .get(`/files/${fileId}/data`)
        .set("X-Token", token);
      expect(resData).to.have.status(200);
      expect(resData.text).to.equal("Hello Webstack!\n");
    });
    it("should return data with published file linked to :id and user authenticated", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/publish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", true);

      const resData = await chai
        .request(app)
        .get(`/files/${fileId}/data`)
        .set("X-Token", token);
      expect(resData).to.have.status(200);
      expect(resData.text).to.equal("Hello Webstack!\n");
    });
    it("should return data with published file linked to :id and user authenticated but not owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/publish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", true);

      const resData = await chai
        .request(app)
        .get(`/files/${fileId}/data`)
        .set("X-Token", token2);
      expect(resData).to.have.status(200);
      expect(resData.text).to.equal("Hello Webstack!\n");
    });
    it("should return data with published file linked to :id and user authenticated and owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${fileId}/publish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", true);

      const resData = await chai
        .request(app)
        .get(`/files/${fileId}/data`)
        .set("X-Token", token);
      expect(resData).to.have.status(200);
      expect(resData.text).to.equal("Hello Webstack!\n");
    });
    it("should return 400 with unpublished folder lined to :id but user unauthenticated", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${folderId}/unpublish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", false);
      expect(res.body).to.have.property("type", "folder");

      const resData = await chai.request(app).get(`/files/${folderId}/data`);

      expect(resData).to.have.status(404);
      expect(resData.body).to.have.property("error", "Not found");
    });
    it("should return 404 with published folder linked to :id but user unauthenticated", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${folderId}/publish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", true);
      expect(res.body).to.have.property("type", "folder");

      const resData = await chai.request(app).get(`/files/${folderId}/data`);

      expect(resData).to.have.status(400);
      expect(resData.body).to.have.property(
        "error",
        "A folder doesn't have content"
      );
    });
    it("should return 404 with unpublished folder linked to :id but user authenticated and not owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${folderId}/unpublish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", false);
      expect(res.body).to.have.property("type", "folder");

      const resData = await chai
        .request(app)
        .get(`/files/${folderId}/data`)
        .set("X-Token", token2);
      expect(resData).to.have.status(404);
      expect(resData.body).to.have.property("error", "Not found");
    });
    it("should return 400 with published folder linked to :id but user authenticated and not owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${folderId}/publish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", true);
      expect(res.body).to.have.property("type", "folder");

      const resData = await chai
        .request(app)
        .get(`/files/${folderId}/data`)
        .set("X-Token", token2);
      expect(resData).to.have.status(400);
      expect(resData.body).to.have.property(
        "error",
        "A folder doesn't have content"
      );
    });
    it("should return 400 with unpublished folder lined to :id but user authenticated and owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${folderId}/unpublish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", false);
      expect(res.body).to.have.property("type", "folder");

      const resData = await chai
        .request(app)
        .get(`/files/${folderId}/data`)
        .set("X-Token", token);
      expect(resData).to.have.status(400);
      expect(resData.body).to.have.property(
        "error",
        "A folder doesn't have content"
      );
    });
    it("should return 400 with published folder linked to :id but user authenticated and owner", async () => {
      const res = await chai
        .request(app)
        .put(`/files/${folderId}/publish`)
        .set("X-Token", token);

      expect(res).to.have.status(200);
      expect(res.body).to.have.property("isPublic", true);
      expect(res.body).to.have.property("type", "folder");

      const resData = await chai
        .request(app)
        .get(`/files/${folderId}/data`)
        .set("X-Token", token);
      expect(resData).to.have.status(400);
      expect(resData.body).to.have.property(
        "error",
        "A folder doesn't have content"
      );
    });

    it("should return 404 error with unpublished file not present locally and user unauthenticated", async () => {
      const res = await chai
        .request(app)
        .get(`/files/${fileIdUnpublishNoLocal}/data`);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should return 404 error with published file not present locally and user unauthenticated", async () => {
      const res = await chai
        .request(app)
        .get(`/files/${fileIdpublishNoLocal}/data`);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should return 404 error with unpublished file not present locally and user auththenticated but not owner", async () => {
      const res = await chai
        .request(app)
        .get(`/files/${fileIdUnpublishNoLocal}/data`)
        .set("X-Token", token2);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should return 404 error with published file not present locally and user auththenticated but not owner", async () => {
      const res = await chai
        .request(app)
        .get(`/files/${fileIdpublishNoLocal}/data`)
        .set("X-Token", token2);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should return 404 error with unpublished file not present locally and user auththenticated but owner", async () => {
      const res = await chai
        .request(app)
        .get(`/files/${fileIdUnpublishNoLocal}/data`)
        .set("X-Token", token);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
    it("should return 404 error with published file not present locally and user auththenticated but owner", async () => {
      const res = await chai
        .request(app)
        .get(`/files/${fileIdpublishNoLocal}/data`)
        .set("X-Token", token);
      expect(res).to.have.status(404);
      expect(res.body).to.have.property("error", "Not found");
    });
  });
});
