-- Flattens a table of tables into a single table
local t = ...
local n, i = {}, 1

for _,u in pairs(t) do
    for _,v in pairs(u) do
        n[i] = v
        i = i + 1
    end
end

return n