def getCRC(s, i, b) {
    java.util.zip.CRC32 crc32 = new java.util.zip.CRC32()
    s.toCharArray().each { crc32.update(it as Byte) }
    [0, 8, 16, 24].collect { i >> it }.each { crc32.update(it as Byte) }
    b.eachWithIndex { v, k -> if (k < b.length - 2) crc32.update(v) }
    return crc32.value as Short
}

def encodeGroup(p) {
    def sb = new StringBuffer()
    def shift = { k -> (k < 10) ? 48 + k : 65 + k - 10 }
    def push = { k -> sb.append(shift(k % 36) as Character) }
    (0..4).collect { p / (36**it) as Integer }.each { push(it) }
    return sb as String
}

def encodeGroups(p) {
    def magic = 0x39aa400 as BigInteger
    def c = (0..4).collect { p / (magic**it) as BigInteger }
    return c.collect { it.mod(magic) as BigInteger }.collect { encodeGroup(it) }.join("-")
}

def getRSAKey(k) {
    def pow = new BigInteger("430d187de8bb3f18fb0a986e6b7163bd", 16)
    def mod = new BigInteger("86f71688cdd2612ca117d1f54bdae029", 16)
    return (new BigInteger(k)).modPow(pow, mod)
}

def getKey(name, id) {
    def p = [1, 14] // product codes: PRODUCT_TYPE = 1, VERSION = 14
    def days = [0, 8].collect {((0 & 0xFFFF) >> it) & 255} // days codes
    def dates = [0, 8, 16, 24].collect {((new Date()).time >> it) & 255} // dates code
    def k = [*p, *dates, *days, 0, 0, 0, 0] as byte[]
    def crc = getCRC(name, id % 0x186A0, k)
    k[10] = crc & 255
    k[11] = (crc >> 8) & 255
    return k as byte[]
}

def makeKey(String name) {
    def id = (new Random()).nextInt(0xCAFE)
    def key = getKey(name, id)
    def cipher = getRSAKey(key)
    def groups = encodeGroups(cipher)
    return "${id}-${groups}".padLeft(35, '0')
}

def user = "jgregorio"
def key = makeKey(user)
println(key)