-- KEYS[1]: 限流的 key（例如：ratelimit:upload:192.168.1.1）
-- ARGV[1]: 限流的时间窗口（例如：10秒）
-- ARGV[2]: 允许的最大请求次数（例如：5次）

-- 1. 获取当前 key 的访问次数
local current = redis.call('GET', KEYS[1])

-- 2. 如果当前次数存在，并且超过了最大限制，直接返回 0（代表限流触发）
if current and tonumber(current) >= tonumber(ARGV[2]) then
    return 0
end

-- 3. 如果当前次数不存在，或者当前次数没有超过最大限制，那么就增加 1 次访问次数 INCR方法：让 Redis 里的计数器加 1。
current = redis.call('INCR', KEYS[1])

-- 4. 如果是第一次访问（次数为1），设置过期时间（时间窗口）
if tonumber(current) == 1 then
    redis.call('EXPIRE', KEYS[1], (ARGV[1]))
end

-- 5. 返回当前的次数（代表允许通过）
return current