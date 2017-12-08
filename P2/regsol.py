import numpy as np
from sklearn import datasets, tree, linear_model
from sklearn.kernel_ridge import KernelRidge
from sklearn.model_selection import cross_val_score
import timeit


def mytraining(X, Y):
    reg = linear_model.ElasticNet()
    reg = reg.fit(X, Y)

    return reg


def test_params(X, Y):
    for i in range(1, 10, 2):
        reg = KernelRidge(kernel="linear", alpha=i)
        reg = reg.fit(X, Y)
        print('kn' + str(i), -cross_val_score(reg, X, Y, cv=5, scoring='neg_mean_squared_error').mean())


def myprediction(X, reg):
    Ypred = reg.predict(X)

    return Ypred
