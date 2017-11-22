import numpy as np
from sklearn import neighbors, datasets, tree, linear_model
from sklearn.model_selection import cross_val_score
from sklearn.externals import joblib

import classsol

#load input data
words = []
with open("words.txt") as file:
    for line in file: 
        line = line.split(' ') #or some other preprocessing
        words.append(line) #storing everything in memory!

X = words[0]

for test in ["wordsclass.npy", "wordsclass2.npy"]:
    print("Testing " + test)
    #load output data
    Y=np.load(test)
    
    f = classsol.features(X)    
    
    clf = classsol.mytraining(f,Y)
      
    Ypred = classsol.myprediction(f, clf)
        
    if (np.sum(Y^Ypred)/len(X))<.05:
        print("Erro bastante baixo. PERFECT!\n")
    elif (np.sum(Y^Ypred)/len(X))<.3:    
        print("Erro nos Q dentro dos limites de tolerância. OK\n")
    else:
        print("Erro nos Q acima dos limites de tolerância. FAILED\n")
