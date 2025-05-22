package com.mrlee.game_store.practice;

import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CompareTest {

    @Test
    void 래퍼클래스는_정렬클래스가_없어도_정렬할수있다() {
        TreeSet<Integer> treeSet = new TreeSet<>(Comparator.reverseOrder());
        treeSet.add(1);
        treeSet.add(2);
        treeSet.add(3);
        treeSet.add(4);
        treeSet.add(5);
        treeSet.add(10);
        treeSet.add(9);
        treeSet.add(8);
        treeSet.add(7);
        treeSet.add(6);
        System.out.println(treeSet.first());
        System.out.println(treeSet.last());
    }

    @Test
    void 배열은_Arrays클래스를_사용하면_쉽게_정렬이_가능하다() {
        int[] arr = new int[5];
        arr[0] = 10;
        arr[1] = 11;
        arr[2] = 20;
        arr[3] = 80;
        arr[4] = 50;
        Arrays.sort(arr);
        System.out.println(Arrays.toString(arr));
    }

    @Test
    void 사용자_정의클래스는_비교자를_구현해야한다() {
        //Collections.sort();
        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(2);
        list.add(10);
        list.sort(Comparator.comparing(e -> e));
        System.out.println(list);
    }

    @Test
    void dogT() {
        Set<Dog> set = new TreeSet<>();
        set.add(new Dog("복실이", 5));
        set.add(new Dog("복실이", 7));

        System.out.println(set);
        TreeSet<Cat> treeSet = new TreeSet<>(new CatComparator());

    }

    static class CatComparator implements Comparator<Cat> {

        @Override
        public int compare(Cat o1, Cat o2) {
            return Integer.compare(o1.getAge(), o2.getAge());
        }
    }

    @Getter
    static class Cat implements Comparator<Cat> {
        private String name;
        private int age;

        public Cat(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int compare(Cat o1, Cat o2) {
            return 0;
        }
    }

    @Getter
    static class Dog implements Comparable<Dog> {
        private String name;
        private int age;

        public Dog(String name, int age) {
            this.name = name;
            this.age = age;
        }

        //내가 구현해야하네?
        @Override
        public int compareTo(Dog o) {
            return this.name.compareTo(o.name);
        }

        //compareTo
        //결과: -1, 0, 1

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Dog dog = (Dog) o;
            return Objects.equals(name, dog.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "Dog{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
