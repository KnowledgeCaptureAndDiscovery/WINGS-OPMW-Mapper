

import sys


def wordcount1():
	with open(sys.argv[2], "w") as w:
		with open(sys.argv[1]) as f:
			count=0
			for line in f:
				linesplitter=line.split(" ")
				for words in linesplitter:
					print words
					if(words=="great"):
						count+=1
			w.write(str(count))

wordcount1()