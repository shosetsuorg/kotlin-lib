local o, f = ...
return function(...)
    return f(o, ...)
end