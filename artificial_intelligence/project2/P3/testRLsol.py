import numpy as np
from sklearn.externals import joblib
from RL import *
import RLsol


for test in [('fmdp1.pkl','traj1.pkl'), ('fmdp2.pkl','traj2.pkl')]:
    
    print("Testing " + test[0])    
    # funcoes auxiliarres
    fmdp = joblib.load(test[0]) 
    # ficheiro com a trajectório de treino             
    traj = joblib.load(test[1]) 
    
    qlearn = RLsol.myRL(7,2,0.9)
    Q = qlearn.traces2Q(traj)
    
    print("Valores Q aprendidos")
    print(qlearn.Q)
    if np.linalg.norm(Q-fmdp.Q)<.3:
        print("Erro nos Q dentro dos limites de tolerância. OK\n")
    else:
        print("Erro nos Q acima dos limites de tolerância. FAILED\n")
    
    # gerar trajectoria aprendida
    J,trajlearn = fmdp.runPolicy(4,5,RLsol.Q2pol(Q))
    
    print("Trajectoria gerada com a politica aprendida")
    print(trajlearn)
    if J>.7:
        print("Recompensa obtida dentro do previsto. OK\n")
    else:
        print("Recompensa obtida abaixo do previsto. FAILED\n")