local o, f = ...
local t, j = {}, 1
for i=0, o:size()-1 do
    local v = o:get(i)
    if f(v) then
        t[j] = v
        j = j + 1
    end
end
return t