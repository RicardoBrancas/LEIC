# -*- coding: utf-8 -*-
"""
Created on Mon Oct 16 20:31:54 2017

@author: mlopes
"""
import numpy as np


def Q2pol(Q, eta=5):
	return np.exp(eta * Q) / np.dot(np.exp(eta * Q), np.array([[1, 1], [1, 1]]))


def alpha(n):
	return 50 / (50 + n)


class myRL:
	def __init__(self, nS, nA, gamma):
		self.nS = nS
		self.nA = nA
		self.gamma = gamma
		self.Q = np.zeros((nS, nA))

	def traces2Q(self, trace):
		Ns = np.zeros((self.nS, self.nA))

		while True:
			lastQ = np.copy(self.Q)

			for j in range(trace.shape[0] - 1):
				s0 = int(trace[j, 0])
				a = int(trace[j, 1])
				s1 = int(trace[j, 2])
				r = int(trace[j, 3])

				self.Q[s0, a] = self.Q[s0, a] + alpha(Ns[s0, a]) * (r + self.gamma * np.max(self.Q[s1, :]) - self.Q[s0, a])
				Ns[s0, a] += 1

			if np.linalg.norm(lastQ - self.Q) < 1e-2:
				break

		return self.Q
