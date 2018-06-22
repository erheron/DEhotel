class Hasher {

    static Long hash(String s) {
        int mod = 1000000009;
        Long res = 0L;
        Long P = 1L;
        for (char c : s.toCharArray()) {
            res += c * P;
            P *= 31;
            res %= mod;
            P %= mod;
        }
        return res;
    }
}
