const userMock = require('./user');
const productMock = require('./product');

module.exports = (app, helper) => {
    userMock(app);
    productMock(app);
};