const path = require('path');

module.exports = [
    {
        mode: 'development',
        entry: {
            'register': './src/main/js/component/register.jsx',
            'login': './src/main/js/component/login.jsx',
            'home': './src/main/js/component/home.jsx',
            'menu': './src/main/js/component/menu.jsx',
            'contacts': './src/main/js/component/contacts.jsx',
            'about': './src/main/js/component/about.jsx',
            'cart': './src/main/js/component/cart.jsx',
            'account': './src/main/js/component/account.jsx',
            'managing': './src/main/js/component/managing.jsx',
            'admin': './src/main/js/component/admin.jsx'
        },
        output: {
            path: path.resolve(__dirname, 'src/main/resources/static/js'),
            filename: '[name].js'
        },
        devtool: 'inline-source-map',
        module: {
            rules: [
                {
                    test: /\.jsx?$/,
                    exclude: /(node_modules)/,
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env', '@babel/preset-react']
                    }
                },
                {
                    test: /\.css$/,
                    use: ['style-loader', 'css-loader']
                }
            ]
        }
    },
    {
        mode: 'development',
        entry: {
            'register': './src/main/js/component/register.jsx',
            'login': './src/main/js/component/login.jsx',
            'home': './src/main/js/component/home.jsx',
            'menu': './src/main/js/component/menu.jsx',
            'contacts': './src/main/js/component/contacts.jsx',
            'about': './src/main/js/component/about.jsx',
            'cart': './src/main/js/component/cart.jsx',
            'account': './src/main/js/component/account.jsx',
            'managing': './src/main/js/component/managing.jsx',
            'admin': './src/main/js/component/admin.jsx'
        },
        output: {
            path: path.resolve(__dirname, 'target/classes/static/js'),
            filename: '[name].js'
        },
        devtool: 'inline-source-map',
        module: {
            rules: [
                {
                    test: /\.jsx?$/,
                    exclude: /(node_modules)/,
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env', '@babel/preset-react']
                    }
                },
                {
                    test: /\.css$/,
                    use: ['style-loader', 'css-loader']
                }
            ]
        }
    }
]