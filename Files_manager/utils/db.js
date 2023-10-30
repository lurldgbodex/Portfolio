import { MongoClient } from "mongodb";

const host = process.env.DB_HOST || "localhost";
const port = process.env.DB_PORT || 27017;
const dbName = process.env.DB_DATABASE || "files_manager";
const url = `mongodb://${host}:${port}`;

class DBClient {
  constructor() {
    this.client = new MongoClient(url, { useUnifiedTopology: true });
    this.connect();
  }

  async connect() {
    await this.client.connect();
    this.db = await this.client.db(dbName);
  }

  async isAlive() {
    try {
      await this.db.command({ ping: 1 });
      return true;
    } catch (err) {
      return false;
    }
  }

  async nbUsers() {
    try {
      const usersCollection = await this.db.collection("users");
      const usersCount = usersCollection.countDocuments();
      return usersCount;
    } catch (err) {
      throw new Error(err);
    }
  }

  async nbFiles() {
    try {
      const filesCollection = await this.db.collection("files");
      const filesCount = filesCollection.countDocuments();
      return filesCount;
    } catch (err) {
      throw new Error(err);
    }
  }

  async users() {
    try {
      const users = await this.db.collection("users");
      return users;
    } catch (err) {
      throw new Error(err);
    }
  }

  async files() {
    try {
      const files = await this.db.collection("files");
      return files;
    } catch (err) {
      throw new Error(err);
    }
  }

  async quit() {
    try {
      await this.client.close();
    } catch (err) {
      throw new Error(err);
    }
  }
}

const dbClient = new DBClient();

module.exports = dbClient;
