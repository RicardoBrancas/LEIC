import numpy as np
from sklearn import datasets, tree, linear_model, kernel_ridge
from sklearn.model_selection import cross_val_score
import timeit


def mytraining(X, Y):
	reg = kernel_ridge.KernelRidge(alpha=0.001, kernel='rbf', gamma=0.1)
	reg = reg.fit(X, Y)

	return reg


def test_param(reg, legend, X, Y):
	reg = reg.fit(X, Y)
	print(type(reg).__name__, str(legend), -cross_val_score(reg, X, Y, cv=5, scoring='neg_mean_squared_error').mean())

	# plt.figure()
	# plt.plot(X, Y, 'k+', label='datatrain')
	# plt.plot(X, reg.predict(X), 'm', label=type(reg).__name__ + str(param))
	# plt.legend(loc=1)
	# plt.show()

	return


def test_params(X, Y):
	test_param(linear_model.LinearRegression(), '', X, Y)

	for gamma in [0.05, 0.1, 0.2]:
		for alpha in [0.1, 0.01, 1e-3]:
			test_param(kernel_ridge.KernelRidge(alpha=alpha, kernel='rbf', gamma=gamma), 'rbf g=' + str(gamma) + ", a=" + str(alpha), X, Y)

	for degree in [2, 3, 4, 5]:
		test_param(kernel_ridge.KernelRidge(kernel='polynomial', degree=degree), degree, X, Y)

	test_param(tree.DecisionTreeRegressor(), '', X, Y)


def myprediction(X, reg):
	Ypred = reg.predict(X)

	return Ypred
