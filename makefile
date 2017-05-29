##Makefile to compile
##Zach Nudelman
##29 March 2017

LIB = ../lib
SRCDIR = src
BINDIR = bin
TESTDIR = test
DOCDIR = doc
CLI = $(LIB)/cli/commons-cli-1.3.1.jar
ASM = $(LIB)/asm/asm-5.0.4.jar:$(LIB)/asm/asm-commons-5.0.4.jar:$(LIB)/asm/asm-tree-5.0.4.jar
JUNIT = $(LIB)/junit/junit-4.12.jar:$(LIB)/junit/hamcrest-core-1.3.jar
JACOCO = $(LIB)/jacoco/org.jacoco.core-0.7.5.201505241946.jar:$(LIB)/jacoco/org.jacoco.report-0.7.5.201505241946.jar:
TOOLS = $(LIB)/tools
JAVAC = /usr/bin/javac
JFLAGS = -g -d $(BINDIR) $(SRCDIR)/*.java -cp $(BINDIR):$(JUNIT)

vpath %.java $(SRCDIR):$(TESTDIR)
vpath %.class $(BINDIR)
vpath %.exec coverage

# define general build rule for java sources
.SUFFIXES:  .java  .class

.java.class:
	$(JAVAC)  $(JFLAGS)  $<

#default rule - will be invoked by make
all: Graph.class \
				SimulatorOne.class \
				Trip.class \
				Victim.class \
				Hospital.class \
				Ambulance.class \
				GraphGenerator.class \
				SimulatorTwo.class \


# Rule for generating documentation
doc:
	javadoc -d $(DOCDIR) $(SRCDIR)/*.java

#Rules for executing applications

One: all
	java -cp ./bin SimulatorOne < test.txt

Two: all
	java -cp ./bin SimulatorTwo

Test: all
	java -cp ./bin Graph

#Self-defined

Everything: clean all Main doc


#Cleans folders
clean:
	@rm -f  $(BINDIR)/*.class
	@rm -Rf doc
	@rm -f Resources/Results/*.csv

cleanResults:
	@rm -f Resources/Results/*.csv
