const path = require('path');
const merge = require('webpack-merge');
const webpack = require('webpack');

// Webpack plugins
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const SpritePlugin = require('svg-sprite-loader/plugin');

// Get the environment
const isProduction = process.env.NODE_ENV === 'production';

// Webpack configurations
const devConfig = require('./config/webpack.dev.config');
const prodConfig = require('./config/webpack.prod.config');

// Webpack configuration
const baseConfig = {

	context: path.resolve(__dirname,'src'),

	// Entry, the start point of the app
	entry: './js/app.js',

	// Where we want to generate the bundle file
	output:{
		path: path.resolve(__dirname, '../resources/webapp/resources/'),
		filename: './js/app.js'
	},

	// Modules
	module: {
		rules: [
			// Javascript
			{
				test: /\.(js|jsx)$/,
				use: ['babel-loader','eslint-loader'],
			},
			// Sass
			{
				test: /\.(scss|sass)$/,
				use: ExtractTextPlugin.extract({
					fallback: 'style-loader',
					use: [ 'css-loader', 'postcss-loader', 'sass-loader']
				})
			},
			// Less
			{
				test: /\.less$/,
				use: ExtractTextPlugin.extract({
					fallback: 'style-loader',
					use: ['css-loader', 'postcss-loader', 'less-loader']
				})
			},
			// Css
			{
				test: /\.(css|sss)$/,
				use: ExtractTextPlugin.extract({
					fallback: 'style-loader',
					use: ['css-loader', 'postcss-loader']
				})
			},
			// Fonts
			{
				test: /\.(ttf|otf|eot|svg|woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?)$/,
				exclude: path.resolve('./src/svg'),
				use: 'file-loader?name=./fonts/[name].[ext]'
			},
			// SVG Sprite
			{
				test: /\.svg$/,
				include: path.resolve('./src/svg'),
				use: [
					{
						loader: 'svg-sprite-loader',
						options: {
							extract: true,
							spriteFilename: './sprite/all.svg'
						}
					},
					{
						loader: 'image-webpack-loader'
					}
				]
			},
			// Images
			{
				test: /\.(jp(e)?g|png|gif)$/,
				use: [
					{
						loader: 'file-loader',
						options: {
							name: '[name].[ext]',
							outputPath: './images/'
						}
					},
					{
						loader: 'image-webpack-loader'
					}
				]
			}
		]
	},

	// Plugins
	plugins:[
		new ExtractTextPlugin('./css/app.css'),
		new CleanWebpackPlugin(['resources/*'], {root: __dirname + '/../resources/webapp/' } ),
		new SpritePlugin(),
		new webpack.ProvidePlugin({
			$: 'jquery',
			jQuery: 'jquery',
			'window.jQuery': 'jquery'
		}),
	]

};


module.exports = merge(baseConfig, isProduction ? prodConfig : devConfig);
