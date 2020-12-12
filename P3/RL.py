import timeit
import numpy as np
import numpy.matlib

from sklearn.externals import joblib


class finiteMDP:

    def Q2pol(self, Q, eta=5):
        return np.exp(eta*Q)/np.dot(np.exp(eta*Q),np.array([[1,1],[1,1]]))
    
    def traces2Q(self, trace):
        
        self.Q = np.zeros((self.nS,self.nA))
        nQ = np.zeros((self.nS,self.nA))
        ii = 0
        while True:            
            for tt in trace:
                #[x, a, y, r]
                nQ[int(tt[0]),int(tt[1])] = nQ[int(tt[0]),int(tt[1])] + 0.01 * (tt[3] + self.gamma * max(nQ[int(tt[2]),:]) - nQ[int(tt[0]),int(tt[1])])
            ii = ii +1  
            err = np.linalg.norm(self.Q-nQ)
            self.Q = np.copy(nQ)
            if err<1e-2:
                break 
        
    def __init__(self, nS, nA, gamma, P=[], R=[], absorv=[]):
        self.nS = nS
        self.nA = nA
        self.gamma = gamma
        if len(P)==0:
            P = np.zeros((nS,nA,nS))
        self.P = P
        if len(R)==0:
            R = np.zeros((nS,nA))
        self.R = R
        self.Q = np.zeros((nS,nA))
        self.V = np.zeros((nS))
        if len(absorv)==0:
            absorv = np.zeros((nS))
        self.absorv = absorv
        
    def T(self, x, a):
        r = self.R[x,a]
        y = self.P[x,a,:]
        
        return y,r
    
    def sT(self, x, a):
        y,r = self.T(x,a)
        
        return y,r
    
    def VI(self):
        nQ = np.zeros((self.nS,self.nA))
        while True:
            self.V = np.max(self.Q,axis=1) 
            for a in range(0,self.nA):
                nQ[:,a] = self.R[:,a] + self.gamma * np.dot(self.P[:,a,:],self.V)
            err = np.linalg.norm(self.Q-nQ)
            self.Q = np.copy(nQ)
            if err<1e-5:
                break
            
        #update policy
        self.V = np.max(self.Q,axis=1) 
        #correct for 2 equal actions
        self.Pol = np.argmax(self.Q, axis=1)
            
        return self.Q
       
    def createTree(self, x0, pol=[], tree=[], depth=3):
        
        if not tree:
            tree = [x0,1,[]]
        else:
            x0 = tree[0]

        if depth==0:
            return []
                        
        for aa in range(0,self.nA):
            if pol[x0,aa]==False:
                continue
                
            for ns in range(0,self.nS):
                if self.P[x0,aa,ns]>0:
                    p = self.P[x0,aa,ns]
                    tree[2].append([ns,tree[1]*p,[]])
                    self.createTree( ns, pol, tree[2][-1], depth-1 )

#        if depth == 1:
#            p = 0
#            for ss in tree[2]:
#                p = p + np.log(ss[1]+1e-16) * ss[1]
#            tree[1] = -1
            
        return tree
    
    
    def computeTreeEntropy(self, tree, findistr=[]):
        
        def auxcomputeTreeEntropy( tree, findistr=[]):
            
            if not tree:
                return findistr
                
            for childs in tree[2]:
                if not childs[2]:
                    findistr.append(childs[1])
                else:
                    auxcomputeTreeEntropy( childs, findistr )
                
            return findistr
            
        e = auxcomputeTreeEntropy( tree )
        
        return sum(np.log(e)*e)
        
            
        
    
    def runPolicy(self, n, x0, pol=[]):
        
        traj = np.zeros((n,4))
        x = x0
        J = 0
        for ii in range(0,n):
            a = np.nonzero(np.random.multinomial( 1, pol[x,:]))[0][0]
            r = self.R[x,a]
            y = np.nonzero(np.random.multinomial( 1, self.P[x,a,:]))[0][0]
            traj[ii,:] = np.array([x, a, y, r])
            J = J + r * self.gamma**ii
            if self.absorv[x]:
                break
            #update state
            x = y
            
        return J,traj
    
    def rollouts(self, n, x0, pol=[], m=50):
        
        Jd = 0
        Jnd = 0
        for nrolls in range(0,m):
            J, traj = self.runPolicy( n, x0, pol)
            # discount missing
            Jnd = Jnd + np.sum(traj[:,3])
            Jd = Jd + J
            
        Jd = Jd/m
        Jnd = Jnd/m
        
        return Jd, Jnd
    
def interprRL( fmdp, query=[] ):
    
    start = timeit.timeit()
    #fmdp2 = finiteMDP( fmdp.nS , fmdp.nA, 0.99, fmdp.P, fmdp.R)
    Q = fmdp.VI()
    #Q2 = fmdp2.VI()
    pol = fmdp.Q2pol(Q, eta=5)
    poll = pol>=0.5
    
    #print(timeit.timeit()-start)
    
    if not query:
        b = int(input("Why A? Why A in X? insert X: "))
    else:
        b = query[0]
        
    print("Because it is the biggest Q-value")
    print(fmdp.Q[b,:])
    
    print("Because the alternatives would be")
    pol[b,:]=[0,1]
    Jd,Jnd = fmdp.rollouts(10,b,pol)
    print("discounted " + str(Jd) + " non discounted " + str(Jnd))
    pol[b,:]=[1,0]
    Jd,Jnd = fmdp.rollouts(10,b,pol)
    print("discounted " + str(Jd) + " non discounted " + str(Jnd))
    print(Q[b,:])
    #print(Q2[3,:])
    #print(timeit.timeit()-start)

    print("Because the entropy would be: ")    
    poll[b,:]=[False,True]
    t = fmdp.createTree( b, poll )
    e = fmdp.computeTreeEntropy( t )
    print(e)
    print("And the alternative: ")
    poll[b,:]=[True,False]
    t = fmdp.createTree( b, poll )
    e = fmdp.computeTreeEntropy( t )
    print(e)  
    #print(timeit.timeit()-start)
#    
### Env 1
#Pl = np.zeros((7,2,7))
#Pl[0,0,1]=1
#Pl[1,0,2]=1
#Pl[2,0,3]=1
#Pl[3,0,4]=1
#Pl[4,0,5]=1
#Pl[5,0,6]=0.9
#Pl[5,0,5]=0.1
#Pl[6,0,6]=1
#  
#Pl[0,1,0]=1
#Pl[1,1,1]=0
#Pl[1,1,0]=1
#Pl[2,1,1]=1
#Pl[3,1,2]=1
#Pl[4,1,3]=1
#Pl[5,1,4]=1    
#Pl[6,1,5]=1
#    
#Rl = np.zeros((7,2))
#Rl[[0,6],:]=1
#absorv = np.zeros((7,1))
##absorv[[0,6]]=1
# 
#fmdp = finiteMDP(7,2,0.9,Pl,Rl,absorv)
##interprRL( fmdp, [3] )
#pol = fmdp.VI()
#pol=pol*0+0.5
#J,traj = fmdp.runPolicy(500,1,pol)
#print(traj)
#
#joblib.dump(fmdp, 'fmdp.pkl') 
#
#print(fmdp.Q)
#fmdp.traces2Q(traj)
#print(fmdp.Q)
#
#joblib.dump(traj, 'traj.pkl') 
#
