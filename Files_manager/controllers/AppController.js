import redisClient from "../utils/redis";
import dbClient from "../utils/db";

const AppController = {
  /**
   * getStatus - gets the status of database
   *
   * @returns - redis and mongodb database status
   */
  async getStatus(req, res) {
    try {
      const redisStatus = await redisClient.isAlive();
      const dbStatus = await dbClient.isAlive();

      return res.json({ redis: redisStatus, db: dbStatus });
    } catch (err) {
      return res.status(500).json({ message: err.message });
    }
  },

  /**
   * getStats - gets the statistics counts of files and user collections in mongodb databas
   *
   * @returns - no of files and no of users in mongodb database
   */
  async getStats(req, res) {
    try {
      const nbUser = await dbClient.nbUsers();
      const nbFiles = await dbClient.nbFiles();
      return res.status(200).json({ users: nbUser, files: nbFiles });
    } catch (err) {
      return res.json({ message: err.message });
    }
  },
};

module.exports = AppController;
