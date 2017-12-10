.PHONY: all clean boston letter
all: boston letter

boston: resources/housing.data
letter: resources/letter.data

resources/housing.data:
	wget -O $@ https://archive.ics.uci.edu/ml/machine-learning-databases/housing/housing.data

resources/letter.data:
	wget -O $@ https://archive.ics.uci.edu/ml/machine-learning-databases/letter-recognition/letter-recognition.data


resources/mnist.scale: resources/mnist.scale.bz2
	(cd resources; bunzip2 mnist.scale.bz2)
resources/mnist.scale.bz2:
	wget -O ./resources/mnist.scale.bz2 \
		https://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/multiclass/mnist.scale.bz2


resources/mnist.scale.t: resources/mnist.scale.t.bz2
	(cd resources; bunzip2 mnist.scale.t.bz2)
resources/mnist.scale.t.bz2:
	wget -O $@ \
		https://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/multiclass/mnist.scale.t.bz2

clean:
	\rm -rf resources/*.data
