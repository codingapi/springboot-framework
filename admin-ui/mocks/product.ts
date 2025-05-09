import Mock from "mockjs";
import webpackMockServer from "webpack-mock-server";

export default webpackMockServer.add((app, helper) => {
    app.get('/api/products', (req, res) => {
        const products = Mock.mock({
            'list|100': [{
                'id|+1': 1,
                'name': '@name',
                'price|100-1000': 1,
            }]
        }).list;

        res.json({
            success: true,
            data:{
                list:products,
                total: products.length
            },
        });
    });
});
