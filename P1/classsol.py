import numpy as np
from sklearn import neighbors, datasets, tree, linear_model

from sklearn.externals import joblib
import timeit

from sklearn.model_selection import cross_val_score


def features(X):
    F = np.zeros((len(X), 5))
    for x in range(0, len(X)):
        F[x, 0] = len(X[x])
        F[x, 1] = sum(letter in 'aeiouAEIOU' for letter in X[x])
        F[x, 2] = sum(letter in 'aeiouAEIOU' for letter in X[x]) % 2 == 0
        F[x, 3] = (len(X[x]) - sum(letter in 'aeiouAEIOU' for letter in X[x])) % 2 == 0  # nr de consoantes par
        F[x, 4] = X[x].count('a')
    return F


def mytraining(f, Y):
    reg = neighbors.KNeighborsClassifier(1)
    reg = reg.fit(f, Y)

    return reg


def myprediction(f, reg):
    Ypred = reg.predict(f)

    return Ypred
