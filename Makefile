.PHONY: all clean doc iris boston letter

all: iris boston letter

iris: assets/iris.data
boston: assets/housing.data
letter: assets/letter.data
mnist: assets/mnist.scale assets/mnist.scale.t
#diabetes: assets/diabetes-data.tar.Z

assets/iris.data:
	wget -O $@ https://archive.ics.uci.edu/ml/machine-learning-databases/iris/iris.data

assets/housing.data:
	wget -O $@ https://archive.ics.uci.edu/ml/machine-learning-databases/housing/housing.data

assets/letter.data:
	wget -O $@ https://archive.ics.uci.edu/ml/machine-learning-databases/letter-recognition/letter-recognition.data

assets/mnist.scale: assets/mnist.scale.bz2
	(cd assets; bunzip2 mnist.scale.bz2)
assets/mnist.scale.bz2:
	wget -O ./assets/mnist.scale.bz2 \
		https://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/multiclass/mnist.scale.bz2

assets/mnist.scale.t: assets/mnist.scale.t.bz2
	(cd assets; bunzip2 mnist.scale.t.bz2)
assets/mnist.scale.t.bz2:
	wget -O $@ \
		https://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/multiclass/mnist.scale.t.bz2

#assets/diabetes-data.tar.Z:
#	wget -O $@ https://archive.ics.uci.edu/ml/machine-learning-databases/diabetes/diabetes-data.tar.Z

doc:
	lein codox

serv_doc: doc
	(cd target/doc && python -m SimpleHTTPServer 5000)

clean:
	\rm -rf assets/*.data
