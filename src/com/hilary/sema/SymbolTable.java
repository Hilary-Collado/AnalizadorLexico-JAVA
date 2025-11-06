package com.hilary.sema;
import java.util.*;

public class SymbolTable {
        private final Deque<Map<String,String>> scopes = new ArrayDeque<>();
        public SymbolTable(){ push(); }
        public void push(){ scopes.push(new HashMap<>()); }
        public void pop(){ scopes.pop(); }
        public boolean declare(String name, String type){
            Map<String,String> top = scopes.peek();
            if(top.containsKey(name)) return false;
            top.put(name, type); return true;
        }
        public String lookup(String name){
            for(Map<String,String> m: scopes) if(m.containsKey(name)) return m.get(name);
            return null;
        }
}
