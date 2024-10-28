const Mock = require('mockjs');

module.exports = (app, helper) => {
    app.get('/api/products', (req, res) => {

        const products = Mock.mock({
            'list|100': [{
                'id|+1': 1,
                'name': '@name',
                'price|100-1000': 1,
            }]
        }).list;

        res.json(products);
    });
};