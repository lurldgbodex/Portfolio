import dbClient from "../utils/db";
import redisClient from "../utils/redis";
import sha1 from "sha1";
import { ObjectID } from "mongodb";
import { userQueue } from "../worker";

const UsersController = {
  /**
   * postNew creates a new user with email and password
   *
   * @returns - created user email when successful or error if failure
   */
  async postNew(req, res) {
    try {
      // retrieve user info from req body with validation
      const { email, password } = req.body;
      if (!email) {
        return res.status(400).json({ error: "Missing email" });
      }
      if (!password) {
        return res.status(400).json({ error: "Missing password" });
      }

      // check db connection and retrieve users colletions
      const dbConnected = await dbClient.isAlive();
      if (!dbConnected) {
        return res.status(400).json({ error: "DB not connected" });
      }
      const users = await dbClient.users();
      const findUser = await users.findOne({ email });
      if (findUser) {
        return res.status(400).json({ error: "Already exist" });
      }

      //create a new user using the req info
      const newUser = {
        email,
        password: sha1(password),
      };
      const user = await users.insertOne(newUser);
      const { _id } = user.ops[0];
      const job = await userQueue.add({ userId: _id });
      console.log(`Job userQueue ${job.id} added to queue`);

      return res.status(201).json({ id: _id, email });
    } catch (err) {
      return res.json({ error: err.message });
    }
  },

  /**
   * getMe - gets the user details
   *
   * @returns - user details on success
   */
  async getMe(req, res) {
    try {
      // retrieve user based on token
      const token = req.headers["x-token"];
      const key = `auth_${token}`;
      const userId = await redisClient.get(key);

      const users = await dbClient.users();
      const user = await users.findOne({ _id: ObjectID(userId) });
      if (!user) {
        return res.status(401).json({ error: "Unauthorized" });
      }

      return res.status(200).json({ id: user._id, email: user.email });
    } catch (err) {
      return res.json({ error: err.message });
    }
  },
};

module.exports = UsersController;
