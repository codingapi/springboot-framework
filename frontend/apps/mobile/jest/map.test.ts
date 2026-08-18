import '@testing-library/jest-dom';


test("map", () => {
    const map = new Map();
    map.set('key', 'value');
    expect(map.get('key')).toBe('value');
    expect(map.has('key')).toBe(true);
    expect(map.size).toBe(1);
    map.delete('key');
    expect(map.has('key')).toBe(false);
    expect(map.size).toBe(0);
})