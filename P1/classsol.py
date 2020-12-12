#83533, Mihail Brinza; 83557, Ricardo Brancas; grupo 4
import numpy as np
from sklearn import neighbors, datasets, tree, linear_model

from sklearn.externals import joblib
import timeit

from sklearn.model_selection import cross_val_score


def features(X):
	F = np.zeros((len(X), 5))
	for x in range(0, len(X)):
		F[x, 0] = len(X[x]) % 2
		F[x, 1] = X[x].count('z') % 2
		F[x, 2] = sum(letter in 'aeiouAEIOU' for letter in X[x]) % 2
		F[x, 3] = sum(letter not in 'aeiouAEIOU' for letter in X[x]) % 2
		F[x, 4] = X[x].count('a')
	return F


def mytraining(f, Y):
	reg = tree.DecisionTreeClassifier()
	reg = reg.fit(f, Y)
	return reg


def test_params(f, Y):
	for i in range(1, 10, 2):
		reg = neighbors.KNeighborsClassifier(i)
		reg = reg.fit(f, Y)
		print('kn' + str(i), np.average(cross_val_score(reg, f, Y, cv=5, scoring='f1')))

	reg = tree.DecisionTreeClassifier()
	reg = reg.fit(f, Y)
	print('dt', np.average(cross_val_score(reg, f, Y, cv=5, scoring='f1')))


def myprediction(f, reg):
	Ypred = reg.predict(f)

	return Ypred
