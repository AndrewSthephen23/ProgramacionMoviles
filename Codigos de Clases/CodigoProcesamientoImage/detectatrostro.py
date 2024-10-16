import cv2 # importar la libreria opencv
image = cv2.imread("rostro4.jpg")
face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
faces = face_cascade.detectMultiScale(gray, 1.3,5,minSize=(10, 10))
existe = "no"
for (x,y,w,h) in faces:
    existe = "si"
    break
cv2.imshow("Evalua rostro", image)
print(existe)
