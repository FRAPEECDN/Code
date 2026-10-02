db = db.getSiblingDB('appdb');

db.createCollection('users');

db.users.insertOne({
  username: 'AdminUser',
  createdAt: new Date()
});
