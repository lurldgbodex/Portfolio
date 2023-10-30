import dbClient from "../utils/db";
import redisClient from "../utils/redis";
import sha1 from "sha1";
import { v4 as uuidv4 } from "uuid";

const AuthController = {
  /**
   * sign a new user in by generating a new authentication token
   *
   * @returns - authentication token valid for 24 hours
   */
  async getConnect(req, res) {
    try {
      const authHeader = req.headers["authorization"];

      if (!authHeader) {
        return res.status(401).json({ error: "Unauthorized" });
      }
      const authData = authHeader.split(" ")[1];

      const credentials = Buffer.from(authData, "base64").toString();
      const [email, password] = credentials.split(":");

      const users = await dbClient.users();
      const authenticateUser = await users.findOne({
        email,
        password: sha1(password),
      });

      if (!authenticateUser) {
        return res.status(401).json({ error: "Unauthorized" });
      }

      const token = uuidv4();
      const key = `auth_${token}`;
      const value = authenticateUser._id.toString();

      await redisClient.set(key, value, 24 * 60 * 60);

      return res.status(200).json({ token: token });
    } catch (err) {
      return res.json({ error: err.message });
    }
  },

  /**
   * getDisconnect - sign-out user based on the token
   *
   * @returns - nothing
   */
  async getDisconnect(req, res) {
    try {
      const token = req.headers["x-token"];
      const key = `auth_${token}`;
      const userId = await redisClient.get(key);

      if (!userId) {
        return res.status(401).json({ error: "Unauthorized" });
      }

      await redisClient.del(key);

      return res.status(204).send();
    } catch (err) {
      return res.json({ error: err.message });
    }
  },
};

module.exports = AuthController;
