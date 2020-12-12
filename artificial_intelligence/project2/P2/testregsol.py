import numpy as np
from sklearn.kernel_ridge import KernelRidge
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import cross_val_score
import matplotlib.pyplot as plt

import regsol

tres = [.3, 800]
for ii, test in enumerate(["regress.npy", "regress2.npy"]):
	print("Testing " + test)
	X, Y, Xp, Yp = np.load(test)

	# regsol.test_params(X, Y)

	reg = regsol.mytraining(X, Y)

	Ypred = regsol.myprediction(Xp, reg)

	# reg2 = KernelRidge(kernel='polynomial', degree=3)
	# reg2.fit(X, Y)
	# Ypred2 = regsol.myprediction(Xp, reg2)
	#
	# reg3 = LinearRegression()
	# reg3.fit(X, Y)
	# Ypred3 = regsol.myprediction(Xp, reg3)
	#
	# f = open('../p2_' + str(ii) + ".csv", 'w')
	# f.write('xp, yp, ypred1, ypred2, ypred3, x, y\n')
	# for i in range(len(Xp)):
	# 	if i < len(X):
	# 		f.write(str(Xp[i][0]) + ", " + str(Yp[i][0]) + ", " + str(Ypred[i][0]) + ", " + str(Ypred2[i][0]) + ", " + str(Ypred3[i][0]) + ", " + str(X[i][0]) + ", " + str(Y[i][0]) + "\n")
	# 	else:
	# 		f.write(str(Xp[i][0]) + ", " + str(Yp[i][0]) + ", " + str(Ypred[i][0]) + ", " + str(Ypred2[i][0]) + ", " + str(Ypred3[i][0]) + "\n")

	if -cross_val_score(reg, X, Y, cv=5, scoring='neg_mean_squared_error').mean() < tres[ii]:
		print("Erro dentro dos limites de tolerância. OK\n")
	else:
		print("Erro acima dos limites de tolerância. FAILED\n")
	plt.figure()
	plt.plot(Xp, Yp, 'g.', label='datatesting')
	plt.plot(X, Y, 'k+', label='datatrain')
	plt.plot(Xp, Ypred, 'm', label='linregres1')
	plt.legend(loc=1)
	# plt.show()
