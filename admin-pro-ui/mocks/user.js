module.exports = (app, helper) => {
    app.post('/user/login', (req, res) => {
        const username = req.body.username;

        if(username==='admin'){
            res.json({
                success:true,
                data:{
                    'username': username,
                    'token':'test token',
                    'avatar':'/logo.png',
                    'authorities': ['ROLE_ADMIN','ROLE_DEVELOPER'],
                }
            });
            return;
        }

        res.json({
            success:true,
            data:{
                'username': username,
                'token':'test token',
                'avatar':'/logo.png',
                'authorities': ['ROLE_USER'],
            }
        });
    });
};