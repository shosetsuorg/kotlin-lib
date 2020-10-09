local fields = {
    ["title"] = o.setTitle,
    ["link"] = o.setLink,
    ["imageURL"] = o.setImageURL
}

return function(t)
    local o = _Novel()
    if not t then return o end

    for k, v in pairs(t) do
        if fields[k] then fields[k](o, v) end
    end

    return o
end
