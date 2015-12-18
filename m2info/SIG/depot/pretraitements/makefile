exec:
	java -cp build:/usr/share/java/postgis.jar:/usr/share/java/postgresql.jar ImporteurJDBC ../android/res/raw/zones.txt ../android/res/raw/line.data.txt

comp:
	javac src/ImporteurJDBC.java -d build -cp /usr/share/java/postgis.jar:/usr/share/java/postgresql.jar -sourcepath src:../android/src

