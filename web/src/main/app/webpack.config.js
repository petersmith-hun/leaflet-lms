const path = require('path');
const webpack = require('webpack');

// Webpack plugins
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

// Get the environment
const isProduction = process.env.NODE_ENV === 'production';

// Webpack configuration
const config = {

	mode: isProduction ? 'production' : 'development',

	devtool: isProduction ? 'source-map' : 'cheap-module-source-map',

	context: path.resolve(__dirname,'src'),

	// Entry, the start point of the app
	entry: './js/app.js',

	// Where we want to generate the bundle file
	output:{
		path: path.resolve(__dirname, '../resources/webapp/resources/js'),
		filename: './app.js'
	},

	optimization: {
		minimize: isProduction
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
			// Css
			{
				test: /\.(css|sss)$/,
				use: ExtractTextPlugin.extract({
					fallback: 'style-loader',
					use: ['css-loader', 'postcss-loader']
				})
			},
			// Images
			{
				test: /\.(jp(e)?g|png|gif)$/,
				use: [
					{
						loader: 'file-loader',
						options: {
							name: '[name].[ext]',
							outputPath: '../images/'
						}
					},
					{
						loader: 'image-webpack-loader'
					}
				]
			},
			// Fonts
			{
				test: /\.(ttf|otf|eot|svg|woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?)$/,
				use: 'file-loader?name=../fonts/[name].[ext]'
			},
		]
	},

	// Plugins
	plugins:[
		new ExtractTextPlugin('../css/app.css'),
		new OptimizeCssAssetsPlugin({
			assetNameRegExp: /app\.css$/g,
			cssProcessor: require('cssnano'),
			cssProcessorPluginOptions: {
				preset: ['default', { discardComments: { removeAll: true } }],
			},
			canPrint: true
		}),
		new CleanWebpackPlugin({
			dry: false,
			cleanOnceBeforeBuildPatterns: ['/../resources/webapp/resources/*'],
			dangerouslyAllowCleanPatternsOutsideProject: true
		}),
		new webpack.ProvidePlugin({
			$: 'jquery',
			jQuery: 'jquery',
			'window.jQuery': 'jquery'
		}),
	]

};


module.exports = config;
