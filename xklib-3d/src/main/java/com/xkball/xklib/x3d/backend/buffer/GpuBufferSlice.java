package com.xkball.xklib.x3d.backend.buffer;

import com.xkball.xklib.x3d.api.render.IGpuBuffer;

public record GpuBufferSlice(IGpuBuffer buffer, long offset, long length){}