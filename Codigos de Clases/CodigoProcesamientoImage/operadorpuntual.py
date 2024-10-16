import numpy as np
from PIL import Image

def inverso(P):
    Q = 255 - P
    return Q
def identidad(P):
    Q = P
    return Q
def binarizar(P,u):
    Q = P>=u# resultado bool
    return Q*255
def binarizarInverso(P,u):
    Q = P<u# resultado bool
    return Q*255
def binarizar2(P,u1,u2):
    Q1 = u1<=P# resultado bool
    Q2 = P<=u2# resultado bool    
    return (Q1*Q2)*255
    
imgGray = Image.open("paisaje.jpg").convert("L")
imgGray.show()# mostrar la imagen
imgNP = np.array(imgGray)#imagen de pillow a matrix np
#resultado = identidad(imgNP)#trasladar pixel a pixel de p a q
resultado = inverso(imgNP)#convertir el valor de q = 255 - p

#resultado = binarizar(imgNP,128)
#resultado = binarizarInverso(imgNP,128)
#resultado = binarizar2(imgNP,90,180)
im = Image.fromarray(resultado)#la matriz np pasarlo a imagen pillow
im.save("inverso.tif")
im.show()
