.PHONY: all boston
all: boston

boston: resources/housing.data

resources/housing.data:
	wget -O $@ https://archive.ics.uci.edu/ml/machine-learning-databases/housing/housing.data

